package service;
//Aljory
import dao.BookingDAO;
import dao.PaymentDAO;
import dao.RoomDAO;
import model.Booking;
import model.Payment;
import model.Room;
import util.AuditLogger;
import util.InputValidator;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PaymentService{

    private PaymentDAO paymentDAO = new PaymentDAO();
    private BookingDAO bookingDAO = new BookingDAO();
    private RoomDAO roomDAO = new RoomDAO();
    private InputValidator inputValidator = new InputValidator();
    private AuditLogger auditLogger = new AuditLogger();
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    //Payment Processing
    public boolean processPayment(int bookingId, double amount, String method){
        if (!inputValidator.validateAmount(amount))return false;
        if (!inputValidator.validateText(method)) return false;

        double expectedTotal = calculateTotal(bookingId);
        if(amount != expectedTotal){
            System.err.println("Amount does not match. Expected Total: "+expectedTotal);
            return false;
        }

        Payment payment = new Payment();
        payment.setBookingId(bookingId);
        payment.setAmount(amount);
        payment.setMethod(method);
        payment.setTimestamp(new Date());
        payment.setStatus("PAID");

        boolean saved = paymentDAO.savePayment(payment);
        if(saved){
            auditLogger.log(bookingId,"PROCESS_PAYMENT",
                    "Payment of "+ amount +" via "+ method +" for booking "+bookingId);
        }
        return saved;
    }

    //To calculate Total
    public double calculateTotal(int bookingId){
        Booking booking = bookingDAO.findById(bookingId);
        if(booking == null) return 0;

        Room room = roomDAO.findById(booking.getRoomId());

        try {
            Date checkIn  = sdf.parse(booking.getCheckInDate());
            Date checkOut = sdf.parse(booking.getCheckOutDate());
            long diff = checkOut.getTime() - checkIn.getTime();
            long nights = diff / (1000*60*60*24);
            return room.getRate() * nights;
        } catch (Exception e) {
            System.err.println("Date parsing error: " + e.getMessage());
            return 0;
        }
    }
}