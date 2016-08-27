package translate.abs;

public interface IWordTranslate extends ITranslate {

	/**
	 * 基本单词释义
	 * 
	 * @return
	 * @throws Exception
	 */
	public String getBasicInfo() throws Exception;

	/**
	 * 基本实例
	 * 
	 * @return
	 * @throws Exception
	 */
	public String getExampleBasic() throws Exception;

	/**
	 * 高级实例
	 * 
	 * @return
	 * @throws Exception
	 */
	public String getExampleAdvance() throws Exception;

	/**
	 * 英英释义
	 * 
	 * @return
	 * @throws Exception
	 */
	public String getEngEng() throws Exception;

	/**
	 * 短语搭配
	 * 
	 * @return
	 * @throws Exception
	 */
	public String getCollocation() throws Exception;

	/**
	 * 句型句式
	 * 
	 * @return
	 * @throws Exception
	 */
	public String getSyntax() throws Exception;

	/**
	 * 近义词
	 * 
	 * @return
	 */
	public String getSimilar();

	/**
	 * 反义词
	 * 
	 * @return
	 */
	public String getAntonym();
}
