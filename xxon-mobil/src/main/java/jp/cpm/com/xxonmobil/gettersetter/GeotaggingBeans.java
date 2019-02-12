package jp.cpm.com.xxonmobil.gettersetter;

public class GeotaggingBeans {
	
	
	
	public String store_Id;
	public String url1;
	public String url2;
	public String status;

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String image;
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}

	public String url3;
	public double Latitude ;
	public double Longitude;
	
	public String getStore_Id() {
		return store_Id;
	}
	public void setStore_Id(String store_Id) {
		this.store_Id = store_Id;
	}
	public double getLatitude() {
		return Latitude;
	}
	public void setLatitude(double d) {
		Latitude = d;
	}
	public double getLongitude() {
		return Longitude;
	}
	public void setLongitude(double d) {
		Longitude = d;
	}
	
	
	public void setUrl1(String url1)
	{
		
		this.url1=url1;
	}
	
	public String getUrl1()
	{
		
		return url1;
	}
	
	public void setUrl2(String url2)
	{
		
		this.url2=url2;
	}
	
	public String getUrl2()
	{
		
		return url2;
	}
	
	public void setUrl3(String url3)
	{
		
		this.url3=url3;
	}
	
	public String getUrl3()
	{
		
		return url3;
	}

}
