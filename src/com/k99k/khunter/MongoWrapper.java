/**
 * 
 */
package com.k99k.khunter;

import java.util.Map;
import java.util.Set;

import org.bson.BSONObject;

import com.mongodb.DBObject;

/**
 * 把KObject包装成Mongodb可用的DBObject
 * @author keel
 *
 */
public class MongoWrapper implements DBObject {

	/**
	 * 包装KObject,并新增_id字段,以适应mongodb
	 * @param kobj KObject
	 */
	public MongoWrapper(KObject kobject) {
		this.kobj = kobject;
		//this.kobj.setProp("_id", kobj.getId());
	}
	
	
	/**
	 * 实际被包装的KObject
	 */
	private KObject kobj;
	
	private boolean isPartial = false;
	
	
	
	/**
	 * @return kobj KObject
	 */
	public final KObject getKobj() {
		return kobj;
	}


	/* (non-Javadoc)
	 * @see com.mongodb.DBObject#isPartialObject()
	 */
	@Override
	public boolean isPartialObject() {
		return isPartial;
	}

	/* (non-Javadoc)
	 * @see com.mongodb.DBObject#markAsPartialObject()
	 */
	@Override
	public void markAsPartialObject() {
		this.isPartial = true;
	}

	/* (non-Javadoc)
	 * @see org.bson.BSONObject#containsField(java.lang.String)
	 */
	@Override
	public boolean containsField(String arg0) {
		return kobj.containsProp(arg0);
	}

	/* (non-Javadoc)
	 * @see org.bson.BSONObject#containsKey(java.lang.String)
	 */
	@Override
	public boolean containsKey(String arg0) {
		return kobj.containsProp(arg0);
	}

	/* (non-Javadoc)
	 * @see org.bson.BSONObject#get(java.lang.String)
	 */
	@Override
	public Object get(String arg0) {
		return kobj.getProp(arg0);
	}

	/* (non-Javadoc)
	 * @see org.bson.BSONObject#keySet()
	 */
	@Override
	public Set<String> keySet() {
		return kobj.getPropMap().keySet();
	}

	/* (non-Javadoc)
	 * @see org.bson.BSONObject#put(java.lang.String, java.lang.Object)
	 */
	@Override
	public Object put(String key, Object v) {
		return kobj.setProp(key, v);
	}

	/* (non-Javadoc)
	 * @see org.bson.BSONObject#putAll(org.bson.BSONObject)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void putAll(BSONObject arg0) {
		kobj.getPropMap().putAll(arg0.toMap());
	}

	/* (non-Javadoc)
	 * @see org.bson.BSONObject#putAll(java.util.Map)
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void putAll(Map arg0) {
		kobj.getPropMap().putAll(arg0);
	}

	/* (non-Javadoc)
	 * @see org.bson.BSONObject#removeField(java.lang.String)
	 */
	@Override
	public Object removeField(String arg0) {
		return kobj.removeProp(arg0);
	}

	/* (non-Javadoc)
	 * @see org.bson.BSONObject#toMap()
	 */
	@Override
	public Map<String, ?> toMap() {
		return kobj.getPropMap();
	}

}
