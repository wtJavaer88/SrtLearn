package translate.site.baidu;

import com.alibaba.fastjson.JSONArray;

import translate.abs.IWordTranslate;

public class BaiduWordTranslate extends BaiduTranslate implements IWordTranslate {
	public BaiduWordTranslate(String engKeyword) {
		super(engKeyword);
	}

	public BaiduWordTranslate(String from, String to, String engKeyword) {
		super(from, to, engKeyword);
	}

	public String getBasicInfo() throws Exception {
		JSONArray jsonArray = getJsonObject().getJSONObject("dict_result").getJSONObject("simple_means")
				.getJSONArray("symbols");
		if (jsonArray != null && !jsonArray.isEmpty())
			return jsonArray.get(0).toString();
		return null;
	}

	public String getExampleBasic() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public String getExampleAdvance() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public String getEngEng() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public String getCollocation() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public String getSyntax() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public String getSimilar() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getAntonym() {
		// TODO Auto-generated method stub
		return null;
	}

}
