public class Room {
    private int roomId;
    private String roomNumber;
    private String roomType;
    private double rate;
    private String status;
    private String cleaningStatus;

    //Getters:
    public int getRoomId(){
        return roomId;
    }

    public String getRoomNumber(){
        return roomNumber;
    }

    public String getRoomType(){
        return roomType;
    }

    public double getRate(){
        return rate;
    }

    public String getStatus(){
        return status;
    }

    public String getCleaningStatus(){
        return cleaningStatus;
    }

    //Setters:
    public void setRoomId(int roomId){
        this.roomId=roomId;
    }

    public void setRoomNumber(String roomNumber){
        this.roomNumber=roomNumber;
    }

    public void setRoomType(String roomType){
        this.roomType=roomType;
    }

    public void setRate(double rate){
        this.rate=rate;
    }

    public void setStatus(String status){
        this.status=status;
    }

    public void setCleaningStatus(String cleaningStatus){
        this.cleaningStatus=cleaningStatus;
    }
}