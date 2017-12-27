package com.hision.erp.service;

public interface ShelfService {

	/**
	 * 物料从库存导入货架
	 * 
	 * @param code	物料编码
	 * @param count 导入数量
	 * @param name	物料名称
	 * @param shelfCode 货架编号
	 * @param shelfName 货架名称
	 */
	public void importShelf(String code, int count, String name, String shelfCode, String shelfName) ;
	
	/**
	 * 判断该物料库存是否充足
	 * 
	 * @param code 物料编码 
	 * @param count 需要从库存导入货架的数量
	 * @return
	 */
	boolean isCountEnoughForImport(String code, int count);
	
	/**
	 * 
	 * 判断该货架物料是否充足
	 * 
	 * @param code	物料编码
	 * @param shelfCode 货架编号
	 * @param count 需要从货架导出的数量
	 * @return
	 */
	boolean isCountEnoughForExport(String code, String shelfCode, int count);
	
	/**
	 * 物料从货架导出
	 * 
	 * @param code	物料编码
	 * @param count 导入数量
	 * @param name	物料名称
	 * @param shelfCode 货架编号
	 * @param shelfName 货架名称
	 */
	public void exportShelf(String code, int count, String name, String shelfCode, String shelfName);
	
	/**
	 * 判断货架是否可以删除
	 * @param shelfId
	 * @return
	 */
	public boolean canDelete(String shelfId);

	public void deleteLinks(String shelfId);
}
