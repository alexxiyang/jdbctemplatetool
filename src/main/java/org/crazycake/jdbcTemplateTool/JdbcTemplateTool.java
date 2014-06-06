package org.crazycake.jdbcTemplateTool;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.crazycake.jdbcTemplateTool.exception.NoColumnAnnotationFoundException;
import org.crazycake.jdbcTemplateTool.exception.NoDefinedGetterException;
import org.crazycake.jdbcTemplateTool.exception.NoIdAnnotationFoundException;
import org.crazycake.jdbcTemplateTool.impl.BatchUpdateSetter;
import org.crazycake.jdbcTemplateTool.impl.ReturnIdPreparedStatementCreator;
import org.crazycake.jdbcTemplateTool.model.CatalogContext;
import org.crazycake.jdbcTemplateTool.model.SqlParamsPairs;
import org.crazycake.jdbcTemplateTool.utils.IdUtils;
import org.crazycake.jdbcTemplateTool.utils.PreparedStatementUtils;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;


public class JdbcTemplateTool {

	private JdbcTemplate jdbcTemplate;
	
	/**
	 * 用于可以指定需要动态切换的库名
	 */
	private static final ThreadLocal<CatalogContext> catalogContextHolder = new ThreadLocal<CatalogContext>(){};

	public static void setCatalogContext(String placeHolder,String catalog){
		catalogContextHolder.set(new CatalogContext(placeHolder,catalog));
	}
	
	public static CatalogContext getCatalogContext(){
		return catalogContextHolder.get();
	}
	
	/**
	 * 动态替换sql的catalog前缀
	 * @param sql
	 * @return
	 */
	public static String changeCatalog(String sql){
		CatalogContext catalogContext = catalogContextHolder.get();
		if(catalogContext != null && catalogContext.getCatalog() != null && catalogContext.getPlaceHolder() != null){
			sql = sql.replace(catalogContext.getPlaceHolder(), catalogContext.getCatalog());
		}
		return sql;
	}
	
	// ------------------------------- select
	// -----------------------------------------//

	/**
	 * 获取对象列表
	 * get a list of clazz
	 * @param sql
	 * @param params
	 * @param clazz
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <T> List<T> list(String sql, Object[] params, Class<T> clazz) {
		
		//动态切换库名
		sql = JdbcTemplateTool.changeCatalog(sql);
		
		List<T> list = null;
		if (params == null || params.length == 0) {
			list = jdbcTemplate.query(sql, new BeanPropertyRowMapper(clazz));
		} else {
			list = jdbcTemplate.query(sql, params, new BeanPropertyRowMapper(clazz));
		}
		
		return list;
	}
	
	/**
	 * 获取总行数
	 * get count
	 * @param sql
	 * @param params
	 * @param clazz
	 * @return
	 */
	public int count(String sql, Object[] params) {
		
		//动态切换库名
		sql = JdbcTemplateTool.changeCatalog(sql);
				
		int rowCount = 0;
		try{
			Map<String, Object> resultMap = null;
			if (params == null || params.length == 0) {
				resultMap = jdbcTemplate.queryForMap(sql);
			} else {
				resultMap = jdbcTemplate.queryForMap(sql, params);
			}
			Iterator<Map.Entry<String, Object>> it = resultMap.entrySet().iterator();
			if(it.hasNext()){
				Map.Entry<String, Object> entry = it.next();
				rowCount = ((Long)entry.getValue()).intValue();
			}
		}catch(EmptyResultDataAccessException e){
			
		}
		
		
		return rowCount;
	}

	/**
	 * 获取一个对象
	 * get object by id
	 * @param sql
	 * @param params
	 * @param clazz
	 * @return
	 * @throws NoIdAnnotationFoundException
	 * @throws NoColumnAnnotationFoundException
	 * @throws NoDefinedGetterException
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public <T> T get(Class clazz, Object id) throws NoIdAnnotationFoundException, NoColumnAnnotationFoundException {

		SqlParamsPairs sqlAndParams = PreparedStatementUtils.getGetFromObject(clazz, id);
		
		//动态切换库名
		String sql = sqlAndParams.getSql();
		sql = JdbcTemplateTool.changeCatalog(sql);
				
		List<T> list = this.list(sql, sqlAndParams.getParams(), clazz);
		if (list.size() > 0) {
			return list.get(0);
		} else {
			return null;
		}
	}

	// ---------------------------- update -----------------------------------//

	/**
	 * 更新某个对象
	 * update object
	 * @param po
	 * @throws Exception
	 */
	public void update(Object po) throws Exception {
		SqlParamsPairs sqlAndParams = PreparedStatementUtils.getUpdateFromObject(po);

		//动态切换库名
		String sql = sqlAndParams.getSql();
		sql = JdbcTemplateTool.changeCatalog(sql);
				
		this.getJdbcTemplate().update(sql, sqlAndParams.getParams());
	}
	
	/**
	 * 批量执行更新操作
	 * 
	 * @param sql
	 * @param paramsList
	 */
	public void batchUpdate(String sql,List<Object[]> paramsList){
		
		//动态切换库名
		sql = JdbcTemplateTool.changeCatalog(sql);
		
		BatchUpdateSetter batchUpdateSetter = new BatchUpdateSetter(paramsList);
		jdbcTemplate.batchUpdate(sql, batchUpdateSetter);
	}

	/**
	 * 保存对象的快捷方法
	 * 如果Id标定的是自增会将自增长的主键自动设置回对象
	 * save object
	 * @param po
	 * @throws Exception
	 */
	public void save(Object po) throws Exception {
		String autoGeneratedColumnName = IdUtils.getAutoGeneratedId(po);
		if(!"".equals(autoGeneratedColumnName)){
			//有自增字段
			int idValue = save(po, autoGeneratedColumnName);
			//把自增的主键值再设置回去
			IdUtils.setAutoIncreamentIdValue(po,autoGeneratedColumnName,idValue);
		}else{
			SqlParamsPairs sqlAndParams = PreparedStatementUtils.getInsertFromObject(po);
			
			//动态切换库名
			String sql = sqlAndParams.getSql();
			sql = JdbcTemplateTool.changeCatalog(sql);
			
			this.getJdbcTemplate().update(sql, sqlAndParams.getParams());
		}		
	}
	
	/**
	 * 保存对象并返回自增长主键的快捷方法
	 * 
	 * @param po
	 * @param autoGeneratedColumnName
	 *            自增长的主键的列名 比如 user_id
	 * @throws Exception
	 */
	private int save(Object po, String autoGeneratedColumnName) throws Exception {

		SqlParamsPairs sqlAndParams = PreparedStatementUtils.getInsertFromObject(po);

		//动态切换库名
		String sql = sqlAndParams.getSql();
		sql = JdbcTemplateTool.changeCatalog(sql);
		
		KeyHolder keyHolder = new GeneratedKeyHolder();
		jdbcTemplate.update(new ReturnIdPreparedStatementCreator(sql, sqlAndParams.getParams(), autoGeneratedColumnName), keyHolder);

		return keyHolder.getKey().intValue();

	}
	
	//-------------------delete-----------------//
	public void delete(Object po) throws Exception{
		
		SqlParamsPairs sqlAndParams = PreparedStatementUtils.getDeleteFromObject(po);
		//动态切换库名
		String sql = sqlAndParams.getSql();
		sql = JdbcTemplateTool.changeCatalog(sql);
		
		jdbcTemplate.update(sql, sqlAndParams.getParams());	
	}

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
}
