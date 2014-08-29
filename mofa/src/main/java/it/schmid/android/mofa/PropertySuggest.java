package it.schmid.android.mofa;

public class PropertySuggest {
	private static PropertySuggest mInstance= null;

	public static Double defaultHour=8.00;

	public PropertySuggest(){}

	public static synchronized PropertySuggest getInstance(){
	    if(null == mInstance){
	        mInstance = new PropertySuggest();
	    }
	    return mInstance;
	}
}
