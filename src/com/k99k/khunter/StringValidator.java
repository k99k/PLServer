/**
 * 
 */
package com.k99k.khunter;

/**
 * String验证器,内含长度比较
 * @author keel
 *
 */
public class StringValidator implements KObjColumnValidate {

	public StringValidator() {
	}
	
	
	private KObjColumnValidate validator;
	
	private int small;
	
	private int big;
	
	private String toStr = StringValidator.class.getName();

	@Override
	public void initType(int type, String[] paras) {
		
		
		if (type == 0) {
			//长度大于某值
			this.validator = new BigThanValidator();
			this.big = Integer.parseInt(paras[0]);
		}else if(type == 1){
			this.validator = new SmallThanValidator();
			this.small = Integer.parseInt(paras[0]);
		}else if(type == 2){
			this.validator = new IntervalValidator();
			this.small = Integer.parseInt(paras[0]);
			this.big = Integer.parseInt(paras[1]);
		}
		
		StringBuilder sb = new StringBuilder(this.toStr);
		sb.append(",");
		sb.append(type);
		sb.append(",");
		for (int i = 0; i < paras.length; i++) {
			sb.append(paras[i]);
			sb.append(",");
		}
		sb.deleteCharAt(sb.length()-1);
		this.toStr = sb.toString();
		
	}

	class BigThanValidator implements KObjColumnValidate{
		@Override
		public boolean validate(Object value) {
			if (value.toString().length() < StringValidator.this.big) {
				return false;
			}
			return true;
		}

		@Override
		public void initType(int type, String[] paras) {
			
		}
	}
	class SmallThanValidator implements KObjColumnValidate{
		@Override
		public boolean validate(Object value) {
			if (value.toString().length() < StringValidator.this.small) {
				return false;
			}
			return true;
		}

		@Override
		public void initType(int type, String[] paras) {
		}
	}
	class IntervalValidator implements KObjColumnValidate{
		@Override
		public boolean validate(Object value) {
			int len = value.toString().length();
			if (len > small && len < big) {
				return true;
			}
			return false;
		}

		@Override
		public void initType(int type, String[] paras) {
		}
	}

	/* (non-Javadoc)
	 * @see com.k99k.khunter.KObjColumnValidate#validate(java.lang.Object)
	 */
	@Override
	public boolean validate(Object value) {
		return this.validator.validate(value);
	}

	@Override
	public String toString() {
		return this.toStr;
	}

	

}
