package org.crazycake.jdbcTemplateTool.utils;

import org.crazycake.jdbcTemplateTool.model.CatalogContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CatalogUtils {
	
	private static Logger logger = LoggerFactory.getLogger(CatalogUtils.class);
	
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
	 * JdbcTemplateTool supports mulitiple catalog query. You can put a placeholder before your table name, JdbcTemplateTool will change this placeholder to real catalog name with the catalog stored in catalogContext. 
	 * @param sql
	 * @return
	 */
	public static String changeCatalog(String sql){
		CatalogContext catalogContext = catalogContextHolder.get();
		if(catalogContext != null && catalogContext.getCatalog() != null && catalogContext.getPlaceHolder() != null){
			sql = sql.replace(catalogContext.getPlaceHolder(), catalogContext.getCatalog());
		}
		logger.debug("real sql: "+sql);
		return sql;
	}
}
