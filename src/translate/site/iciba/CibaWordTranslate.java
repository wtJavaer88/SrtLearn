package translate.site.iciba;

import translate.abs.IWordTranslate;

public class CibaWordTranslate extends CibaTranslate implements IWordTranslate {
	public CibaWordTranslate(String engKeyword) {
		super(engKeyword);
	}

	public String getBasicInfo() throws Exception {
		return getJsonObject().getJSONArray("synonym").toString();
	}

	public String getExampleBasic() throws Exception {
		return null;
	}

	public String getExampleAdvance() throws Exception {
		return null;
	}

	public String getEngEng() throws Exception {
		return null;
	}

	public String getCollocation() throws Exception {
		return null;
	}

	public String getSyntax() throws Exception {
		return null;
	}

	public String getSimilar() {
		return null;
	}

	public String getAntonym() {
		return null;
	}

}
