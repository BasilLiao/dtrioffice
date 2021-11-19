package dtri.com.db.pgsql.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import dtri.com.db.entity.BomGroupEntity;
import dtri.com.db.entity.BomProductEntity;
import dtri.com.db.entity.BomTypeItemEntity;

public interface BomAccessoriesProductDao {

	/** 依照ID查詢 **/
	BomProductEntity queryAccessoriesProductbyId(@Param("id") Integer id);

	/**
	 * 
	 * 一般查詢(清單查詢 )
	 * 
	 * @param name 浮動項目查詢
	 **/
	List<BomProductEntity> queryAccessoriesProduct(@Param("where_product") String where_product,
			@Param("product_limit") String limit);

	ArrayList<BomGroupEntity> queryAccessoriesGroup(@Param("where_group") String where_group,
			@Param("group_limit_in") List<Integer> limit);

	List<BomTypeItemEntity> queryAccessoriesTypeItem(@Param("where_item") String where_item);

	/**
	 * 
	 * 檢查 bom_number 名稱是否重複
	 * */
	BomProductEntity queryAccessories_product_bom_number(BomProductEntity entity);

	/**
	 * 第一次檢查
	 * 
	 **/
	Integer checkedAccessoriesOne(@Param("id") Integer id, @Param("name") String name);

	/**
	 * 添加 產品 /項目群組
	 * 
	 **/
	Integer addedAccessoriesOne(BomProductEntity entity);

	Integer addedAccessoriesOneGroup(BomGroupEntity entity);
	
	Integer addedAccessoriesOneGroupByid(BomGroupEntity entity);

	/**
	 * 取得下個ID(BOM_Product)
	 * 
	 **/
	Integer nextvalBomAccessoriesProduct();
	/**
	 * 取得下個ID(BOM_Group)
	 * 
	 **/
	Integer nextvalBomAccessoriesGroup();

	/**
	 * 產品/群組項目 移除
	 * 
	 **/
	Integer deleteAccessoriesGroupByProduct_id(@Param("product_id") Integer product_id);

	Integer deleteAccessoriesProduct(@Param("id") Integer id);

	Integer deleteAccessoriesGroupOther(BomGroupEntity entity);

	/** 更新 product 與 group名稱 **/

	Integer updateAccessoriesProduct(BomProductEntity entity);

	Integer updateAccessoriesProductKind(@Param("id") Integer id, @Param("kind") Integer kind);

	Integer updateAccessoriesGroup(BomGroupEntity entity);

}