/**
 * 
 */
package com.k99k.khunter;

import org.apache.log4j.Logger;
import com.k99k.tools.StringUtil;

/**
 * 错误码集合,erroCode前两位为1级下标,后三位为2级下标
 * @author keel
 *
 */
public final class ErrorCode {
	
	private ErrorCode(){
	}
	
	/**
	 * 二维数组,第一维为分类，第二维定位到具体的错误码
	 */
	private static final String[][] errorArr = createErr();
	
	
	/**
	 * 实始化所有错误码,二维数组,第一维为分类，第二维定位到具体的错误码
	 * @return String[100][1000]
	 */
	private static final String[][] createErr(){
		
		String[][] errArr = new String[100][1000];
		
		//所有的0位表示ok
		for (int i = 0; i < errArr.length; i++) {
			errArr[i][0] = "ok";
		}
		
		
		//KObjManager,KObjSchema
		errArr[8][1] = "KObjSchema-setColumn json map error:";
		errArr[8][2] = "KObjSchema-setColumn checkType || checkColType error:";
		errArr[8][3] = "KObjSchema-setSubColumn father col not defined. ";
		errArr[8][4] = "KObjSchema-setSubColumn father col is not Map or List. ";
		errArr[8][5] = "KObjSchema-setColumn validator error. ";
		errArr[8][6] = "KObjManager-init ini json error. ";
		errArr[8][7] = "KObjManager-init KObjConfig.newInstance error. ";
		errArr[8][8] = "KObjManager-init Exception throwed. ";
		errArr[8][9] = "KObjSchema-initSchema Exception throwed. ";
		errArr[8][10] = "KObjSchema-setIndex json error. ";
		errArr[8][11] = "KObjSchema-setColumn Exception throwed.";
		errArr[8][12] = "KObjManager-setKObjConfig failed.";
		errArr[8][13] = "KObjManager-addKObj KObjConfig validate failed. ";
		errArr[8][14] = "KObjManager-addKObj dao ==null || dao.add(kobj) failed. ";
		errArr[8][15] = "KObjManager-createKObjConfig key/map is null or KObjConfig.newInstance ==null . ";
		errArr[8][16] = "KObjManager-createKObjConfig key is already exist . ";
		errArr[8][17] = "KObjSchema-applyIndexes failed . ";
		errArr[8][18] = "KObjManager-createKObjConfig findDao failed. ";
		errArr[8][19] = "KObjManager-createKObjConfig dao.add(oneKObj) failed. ";
		errArr[8][20] = "KObjManager-createKObjConfig ks.applyIndexes failed. ";
		errArr[8][21] = "KObjManager-createKObjConfig dao.deleteOne failed. ";
		errArr[8][22] = "KObjManager checkMapTypes error. ";
		errArr[8][23] = "KObjManager-init KObjConfig.newInstance initSchema error:";
		errArr[8][24] = "KObjManager-init KObjConfig.newInstance setDaoConfig error:";
		errArr[8][25] = "KObjSchema-setIndex dao.updateIndex error. ";
		errArr[8][26] = "KObjManager-buildNewData error. ";
		errArr[8][27] = "KObjManager-reBuildNewData error. ";
		
		//KIoc
		errArr[9][10] = "KIoc-saveJsonToFile ini not found.";
		errArr[9][12] = "KIoc-saveJsonToFile json para error.";
		errArr[9][13] = "KIoc-saveJsonToFile writeTxtInUTF8 failed.";
		errArr[9][14] = "KIoc-saveJsonToFile json validate fail";
		errArr[9][15] = "KIoc-loadClassInstance Exception throwed.className:";
		errArr[9][16] = "KIoc-setProp Method can't be found:";
		errArr[9][17] = "KIoc-setProp Exception throwed.propName:";
		errArr[9][18] = "KIoc-setProps one of the props can't be found,para position:";
		errArr[9][19] = "KIoc-setProps Exception throwed.para position:";
		errArr[9][20] = "KIoc-readTxt UnsupportedEncodingException filePath:";
		errArr[9][21] = "KIoc-readTxt IOException filePath:";
		errArr[9][22] = "KIoc-writeTxtInUTF8 IOException throwed.file:";
		errArr[9][23] = "KIoc-updateJsonNode Exception throwed. json:";
		errArr[9][24] = "KIoc-updateJsonNode(ArrayList) Exception throwed. json:";
		errArr[9][25] = "KIoc-updateIniFileNode failed - save bak file failed:";
		errArr[9][26] = "KIoc-backupFile failed - file:";
		errArr[9][26] = "KIoc-saveJsonToFile json map or path is null. file:";
		
		
		//KObjAction
		errArr[15][1] = "KObjAction-checkKObjJson failed.";
		errArr[15][2] = "KObjAction- ";
		errArr[15][3] = "KObjAction-init kobjs node not exist! KObjAction init failed.";
		errArr[15][4] = "KObjAction-init Exception throwed.";
		errArr[15][5] = "KObjAction-createKObjTable dao not found:";
		errArr[15][6] = "KObjAction-createKObjTable JSON error";
		errArr[15][7] = "KObjAction-createKObjTable update kobj.json failed.";
		errArr[15][8] = "KObjAction-createKObjTable set dao Props failed.";
		errArr[15][9] = "KObjAction-createKObjTable DaoManager.addDao failed.";
		errArr[15][10] = "KObjAction-createKObjTable DaoManager.storeDao failed.";
		errArr[15][11] = "KObjAction-updateDao set dao Props failed.";
		errArr[15][12] = "KObjAction-updateDao DaoManager.addDao failed.";
		errArr[15][13] = "KObjAction-updateDao DaoManager.storeDao || save() failed.";
		errArr[15][14] = "KObjAction-updateDao kobjName is not in kobjMap.";
		errArr[15][15] = "KObjAction-updateDao DaoManager.changeDao failed.";
		errArr[15][16] = "KObjAction-updateDao DaoManager.findDao == null";
		errArr[15][20] = "KObjAction-execDaoFunction jsonReq == null or findDao == null. kobjName:";
		errArr[15][21] = "KObjAction-execDaoFunction reqMap == null || (!reqMap.containsKey('req'). kobjName:";
		errArr[15][22] = "KObjAction-execDaoFunction req value error.  kobjName:";
		errArr[15][23] = "KObjAction-execDaoFunction  daoFuncNum error. kobjName:";
		errArr[15][24] = "KObjAction-* kobjName is not in kobjMap. kobjName:";
		errArr[15][25] = "KObjAction-* save failed. kobjName:";
		errArr[15][26] = "KObjAction-changeKObjDao Dao not exist.Create dao first and try again. kobjName:";
		errArr[15][26] = "KObjAction-changeKObjDao DaoManager.findDao == null. kobjName:";
		errArr[15][27] = "KObjAction-addDao addDao failed. daoName:";
		errArr[15][28] = "KObjAction-addDao storeDao failed. daoName:";
		errArr[15][29] = "KObjAction-removeDao DaoManager.removeDao failed. daoName:";
		errArr[15][30] = "KObjAction-updateDaoProps DaoManager.findDao is null. daoName:";
		errArr[15][31] = "KObjAction-updateDaoProps DaoManager.storeDao failed. daoName:";
		errArr[15][32] = "KObjAction- . daoName:";
		
		
		
		
		
		
		
		
		
		
		return errArr;
	}
	
	
	
	
	
	
	
	
//	static{
////		error.put("404", "page not found.");
//		
//		errorArr[1][0] = "000.";
//		errorArr[4][4] = "page not found.";
//	}
	
	
	/**
	 * 获 取错误信息,erroCode前两位为1级下标,后三位为2级下标
	 * @param errorCode 共4-5位int,前两位为1级下标,后三位为2级下标
	 * @return errMsg 若无定义则返回""
	 */
	public static final String getErrorInfo(int errCode){
		//先保证位数正确
		if (errCode>99999 || errCode<1000) {
			return "";
		}
		String str = String.valueOf(errCode);
		//如果是4位则1级下标仅有1位
		int foreLen = (errCode > 9999) ? 2 : 1;
		int errCode1 = Integer.parseInt(str.substring(0,foreLen));
		int errCode2 = Integer.parseInt(str.substring(foreLen));
		return StringUtil.objToStrNotNull(errorArr[errCode1][errCode2]);
	}
	
	/**
	 * 日志记录错误
	 * @param log
	 * @param errorCode
	 * @param e Exception
	 */
	public static final void logError(Logger log,int errorCode,Exception e,String plusInfo){
		log.error(errorCode+" : "+getErrorInfo(errorCode)+plusInfo,e);
	}
	
	/**
	 * 日志记录错误
	 * @param log
	 * @param errorCode
	 */
	public static final void logError(Logger log,int errorCode,String plusInfo){
		log.error(errorCode+" : "+getErrorInfo(errorCode)+plusInfo);
	}
	
	
	/**
	 * 获 取错误信息,erroCode前两位为1级下标,后三位为2级下标
	 * @param errCode1 1-2位int
	 * @param errCode2 3位int 
	 * @return errMsg 若无定义则返回""
	 */
	public static final String getErrorInfo(int errCode1,int errCode2){
		//先保证位数正确
		if (errCode1>100 || errCode1<=0 || errCode2>1000 || errCode2<0) {
			return "";
		}
		return StringUtil.objToStrNotNull(errorArr[errCode1][errCode2]);
	}
	
	/**
	 * 日志记录错误
	 * @param log
	 * @param errCode1 1-2位int
	 * @param errCode2 3位int 
	 * @param e Exception
	 */
	public static final void logError(Logger log,int errCode1,int errCode2,Exception e,String plusInfo){
		log.error(errCode1+"-"+errCode2+" : "+getErrorInfo(errCode1,errCode2)+plusInfo,e);
	}
	
	/**
	 * 日志记录错误
	 * @param log
	 * @param errCode1 1-2位int
	 * @param errCode2 3位int 
	 */
	public static final void logError(Logger log,int errCode1,int errCode2,String plusInfo){
		log.error(errCode1+"-"+errCode2+" : "+getErrorInfo(errCode1,errCode2)+plusInfo);
	}
	
	/**
	 * 获取所有error的String[][]
	 * @return
	 */
	public static final String[][] getErrorArr(){
		return errorArr;
	}
	
	/**
	 * 新增或更新一个error code,errCode共4-5位int,前两位为1级下标,后三位为2级下标
	 * @param errCode1 1-2位int
	 * @param errCode2 3位int 
	 * @param errMsg String
	 */
	public static final void saveErrCode(int errCode1,int errCode2,String errMsg){
		errorArr[errCode1][errCode2] = errMsg;
	}
	
	
	
//	private final static HashMap<String,String> error = new HashMap<String, String>(200);
//
//	/**
//	 * 获 取错误信息
//	 * @param errorCode
//	 * @return errMsg
//	 */
//	public static final String getErrorInfo(String errorCode){
//		return error.get(errorCode);
//	}
//	
//	/**
//	 * 日志记录错误
//	 * @param log
//	 * @param errorCode
//	 * @param e Exception
//	 */
//	public static final void logError(Logger log,String errorCode,Exception e){
//		log.error(errorCode+" : "+error.get(errorCode),e);
//	}
//	
//	/**
//	 * 日志记录错误
//	 * @param log
//	 * @param errorCode
//	 */
//	public static final void logError(Logger log,String errorCode){
//		log.error(errorCode+" : "+error.get(errorCode));
//	}
//	
//	/**
//	 * 获取所有error的Map
//	 * @return
//	 */
//	public static final HashMap<String,String> getErrMap(){
//		return error;
//	}
//	
//	/**
//	 * 新增或更新一个error code
//	 * @param errCode
//	 * @param errMsg
//	 */
//	public static final void saveErrCode(String errCode,String errMsg){
//		error.put(errCode, errMsg);
//	}
}
