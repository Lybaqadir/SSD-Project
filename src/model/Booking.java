//lybaqadir did this file

package model;

/**
 *   BookingDAO reads a row from the "bookings" table and fills this object.
 *   BookingService uses this object to apply business rules (validate, save).
 *   BookingController uses this object to display booking info on screen.
 */
public class Booking {

    private int    bookingId;
    private String guestName;
    private String guestPhone;
    private int    roomId;
    private String checkInDate;
    private String checkOutDate;
    private String status;

    public Booking(int bookingId, String guestName, String guestPhone,
                   int roomId, String checkInDate, String checkOutDate, String status) {
        this.bookingId   = bookingId;
        this.guestName   = guestName;
        this.guestPhone  = guestPhone;
        this.roomId      = roomId;
        this.checkInDate  = checkInDate;
        this.checkOutDate = checkOutDate;
        this.status      = status;
    }

    //GETTERS

    public int    getBookingId()    { return bookingId; }
    public String getGuestName()    { return guestName; }
    public String getGuestPhone()   { return guestPhone; }
    public int    getRoomId()       { return roomId; }
    public String getCheckInDate()  { return checkInDate; }
    public String getCheckOutDate() { return checkOutDate; }
    public String getStatus()       { return status; }

    // SETTERS

    public void setBookingId(int bookingId)       { this.bookingId = bookingId; }
    public void setGuestName(String guestName)    { this.guestName = guestName; }
    public void setGuestPhone(String guestPhone)  { this.guestPhone = guestPhone; }
    public void setRoomId(int roomId)             { this.roomId = roomId; }
    public void setCheckInDate(String checkInDate)   { this.checkInDate = checkInDate; }
    public void setCheckOutDate(String checkOutDate) { this.checkOutDate = checkOutDate; }
    public void setStatus(String status)          { this.status = status; }

    @Override
    public String toString() {
        return "Booking{" +
                "id="          + bookingId   +
                ", guest='"    + guestName   + '\'' +
                ", phone='"    + guestPhone  + '\'' +
                ", roomId="    + roomId      +
                ", checkIn='"  + checkInDate  + '\'' +
                ", checkOut='" + checkOutDate + '\'' +
                ", status='"   + status      + '\'' +
                '}';
    }
}
