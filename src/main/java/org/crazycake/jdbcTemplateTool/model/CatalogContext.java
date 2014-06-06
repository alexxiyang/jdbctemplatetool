package org.crazycake.jdbcTemplateTool.model;

public class CatalogContext {
	private String placeHolder;
	private String catalog;
	
	public CatalogContext(String placeHolder, String catalog){
		this.placeHolder = placeHolder;
		this.catalog = catalog;
	}
	
	public String getPlaceHolder() {
		return placeHolder;
	}
	public void setPlaceHolder(String placeHolder) {
		this.placeHolder = placeHolder;
	}
	public String getCatalog() {
		return catalog;
	}
	public void setCatalog(String catalog) {
		this.catalog = catalog;
	}
	
	
}
