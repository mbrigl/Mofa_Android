package it.schmid.android.mofa;

public class PropertySuggest {
	private static PropertySuggest mInstance= null;

	static Double defaultHour=null;

	protected PropertySuggest(){}

	public static synchronized PropertySuggest getInstance(){
	    if(null == mInstance){
	        mInstance = new PropertySuggest();
	    }
	    return mInstance;
	}
}
