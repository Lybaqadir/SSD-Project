package service;

import dao.BookingDAO;
import dao.RoomDAO;
import model.Booking;
import util.AuditLogger;
import util.InputValidator;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
//Aljory

public class BookingService{

    private BookingDAO bookingDAO = new BookingDAO();
    private RoomDAO roomDAO = new RoomDAO();
    private InputValidator inputValidator = new InputValidator();
    private AuditLogger auditLogger = new AuditLogger();
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    //To create booking
    public boolean createBooking(Booking booking){
        if (!inputValidator.validateText(booking.getGuestName())) return false;
        if (!inputValidator.validateText(booking.getGuestPhone())) return false;
        if (!inputValidator.validateText(booking.getCheckInDate())) return false;
        if (!inputValidator.validateText(booking.getCheckOutDate())) return false;

        try {
            Date checkIn  = sdf.parse(booking.getCheckInDate());
            Date checkOut = sdf.parse(booking.getCheckOutDate());
            if(!checkAvailability(checkIn, checkOut)) return false;
        } catch (Exception e) {
            return false;
        }

        boolean saved = bookingDAO.saveBooking(booking);

        if (saved){
            auditLogger.log(booking.getRoomId(), "CREATE_BOOKING","Booking created for guest: "+booking.getGuestName());
        }
        return saved;
    }

    //To modify bookings
    public boolean modifyBooking(int bookingId, Booking updates){
        if (!inputValidator.validateDate(updates.getCheckInDate())) return false;
        if (!inputValidator.validateDate(updates.getCheckOutDate())) return false;

        try {
            Date checkIn  = sdf.parse(updates.getCheckInDate());
            Date checkOut = sdf.parse(updates.getCheckOutDate());
            if(!checkAvailability(checkIn, checkOut)) return false;
        } catch (Exception e) {
            return false;
        }

        boolean updated = bookingDAO.updateBooking(bookingId, updates);

        if(updated){
            auditLogger.log(bookingId,"MODIFY_BOOKING","Booking ID "+bookingId+" modified.");
        }
        return updated;
    }

    //search booking
    public List<Booking> searchBooking(String criteria){
        return bookingDAO.findByCriteria(criteria);
    }

    //To check availability
    public boolean checkAvailability(Date checkIn, Date checkOut){
        return roomDAO.hasAvailableRoom(checkIn, checkOut);
    }
}