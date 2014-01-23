
public class Passenger {
	private String mName;
	private String mIdNo;
	private String mTypeCode = "1"; // default:����
	private String mTypeName; // ���ˣ���ͯ��ѧ��...
	private String mIdTypeCode = "1"; // default���������֤
	private String mIdTypeName; // �������֤/һ�����֤/�۰�ͨ��֤/̨��ͨ��֤/����
	private String mMobileNo = "13428750489";	
	private String mSexCode;
	
	public String getName(){
		return mName;
	}
	
	public void setName(String name){
		mName = name;
	}
	
	public String getIdNo(){
		return mIdNo;
	}
	
	public void setIdNo(String no){
		mIdNo = no;
	}
	
	public String getTypeCode(){
		return mTypeCode;
	}
	
	public void setTypeCode(String typeCode){
		mTypeCode = typeCode;
	}
	
	public String getIdTypeCode(){
		return mIdTypeCode;
	}
	
	public void setIdTypeCode(String typeCode){
		mIdTypeCode = typeCode;
	}
	
	public String getMobileNo(){
		return mMobileNo;
	}
	
	public void setMobileNo(String no){
		mMobileNo = no;
	}
	
	public String getSexCode(){
		return mSexCode;
	}
	
	public void setSexCode(String code){
		mSexCode = code;
	}
	
	public String toString() {
		return mName + "|" + mIdNo + "|" + mTypeCode + "|" + mIdTypeCode;
	}
	
	public static Passenger fromString(String str){
		String[] passengerStr = str.split("[|]");
		Passenger passenger = new Passenger();
		passenger.setName(passengerStr[0]);
		passenger.setIdNo(passengerStr[1]);
		passenger.setTypeCode(passengerStr[2]);
		passenger.setIdTypeCode(passengerStr[3]);
		return passenger;
	}
}
