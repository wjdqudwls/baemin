package com.uahan.menu.model.dao;

import com.uahan.common.JDBCTemplate;
import com.uahan.menu.model.dto.MenuDTO;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class MenuDAO {

    private Properties prop = new Properties();

    public MenuDAO() {
        try {
            prop.loadFromXML(MenuDAO.class.getClassLoader().getResourceAsStream("mapper/menu-query.xml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 고도화된 검색, 필터링, 다중 정렬 기능이 포함된 메뉴 목록 조회용 메서드입니다.
     * 
     * @param con            DB 연결 객체
     * @param searchQuery    검색어 (메뉴 명칭 기준)
     * @param categoryCode   카테고리 코드 필터링용
     * @param excludeSoldOut 품절 제외 체크박스 상태
     * @param nameSort       이름 기준 정렬 상태 (asc, desc, "")
     * @param priceSort      가격 기준 정렬 상태 (asc, desc, "")
     * @return 필터링 및 정렬된 메뉴 목록
     */
    public List<MenuDTO> selectMenusWithFilter(Connection con, String searchQuery, Integer categoryCode,
            boolean excludeSoldOut, String nameSort, String priceSort) {
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        List<MenuDTO> menuList = null;

        String query = prop.getProperty("selectMenusWithFilter");

        try {
            pstmt = con.prepareStatement(query);

            // 검색어 바인딩: IS NULL 처리와 LIKE 처리를 위해 두 번 바인딩합니다.
            pstmt.setString(1, searchQuery);
            pstmt.setString(2, searchQuery);

            // 카테고리 코드 바인딩: NULL일 경우 DB NULL로 처리하여 필터가 무시되도록 합니다.
            if (categoryCode == null) {
                pstmt.setNull(3, java.sql.Types.INTEGER);
                pstmt.setNull(4, java.sql.Types.INTEGER);
            } else {
                pstmt.setInt(3, categoryCode);
                pstmt.setInt(4, categoryCode);
            }

            // 품절 제외 여부 파라미터 (String으로 변환하여 전달)
            pstmt.setString(5, String.valueOf(excludeSoldOut));

            // 복합 정렬 파라미터 바인딩: SQL의 CASE WHEN 문에 사용됩니다.
            pstmt.setString(6, nameSort);
            pstmt.setString(7, nameSort);
            pstmt.setString(8, priceSort);
            pstmt.setString(9, priceSort);

            rset = pstmt.executeQuery();

            menuList = new ArrayList<>();

            while (rset.next()) {
                MenuDTO menu = new MenuDTO();
                menu.setMenuCode(rset.getInt("menu_code"));
                menu.setMenuName(rset.getString("menu_name"));
                menu.setMenuPrice(rset.getInt("menu_price"));
                menu.setCategoryCode(rset.getInt("category_code"));
                menu.setCategoryName(rset.getString("category_name"));
                menu.setOrderableStatus(rset.getString("orderable_status"));

                menuList.add(menu);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCTemplate.close(rset);
            JDBCTemplate.close(pstmt);
        }

        return menuList;
    }

    /**
     * 특정 메뉴 코드로 메뉴 1건의 상세 정보를 조회합니다.
     */
    public MenuDTO selectMenuById(Connection con, int menuCode) {
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        MenuDTO menu = null;

        String query = prop.getProperty("selectMenuById");

        try {
            pstmt = con.prepareStatement(query);
            pstmt.setInt(1, menuCode);
            rset = pstmt.executeQuery();

            if (rset.next()) {
                menu = new MenuDTO();
                menu.setMenuCode(rset.getInt("menu_code"));
                menu.setMenuName(rset.getString("menu_name"));
                menu.setMenuPrice(rset.getInt("menu_price"));
                menu.setCategoryCode(rset.getInt("category_code"));
                menu.setCategoryName(rset.getString("category_name"));
                menu.setOrderableStatus(rset.getString("orderable_status"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCTemplate.close(rset);
            JDBCTemplate.close(pstmt);
        }

        return menu;
    }

    /**
     * 새로운 메뉴를 데이터베이스에 등록합니다.
     */
    public int insertMenu(Connection con, MenuDTO menu) {
        PreparedStatement pstmt = null;
        int result = 0;

        String query = prop.getProperty("insertMenu");

        try {
            pstmt = con.prepareStatement(query);
            pstmt.setString(1, menu.getMenuName());
            pstmt.setInt(2, menu.getMenuPrice());
            pstmt.setInt(3, menu.getCategoryCode());
            pstmt.setString(4, menu.getOrderableStatus());

            result = pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCTemplate.close(pstmt);
        }

        return result;
    }

    /**
     * 기존 메뉴 정보를 업데이트합니다.
     */
    public int updateMenu(Connection con, MenuDTO menu) {
        PreparedStatement pstmt = null;
        int result = 0;

        String query = prop.getProperty("updateMenu");

        try {
            pstmt = con.prepareStatement(query);
            pstmt.setString(1, menu.getMenuName());
            pstmt.setInt(2, menu.getMenuPrice());
            pstmt.setInt(3, menu.getCategoryCode());
            pstmt.setString(4, menu.getOrderableStatus());
            pstmt.setInt(5, menu.getMenuCode());

            result = pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCTemplate.close(pstmt);
        }

        return result;
    }

    /**
     * 물리적으로 메뉴를 삭제합니다.
     */
    public int deleteMenu(Connection con, int menuCode) {
        PreparedStatement pstmt = null;
        int result = 0;

        String query = prop.getProperty("deleteMenu");

        try {
            pstmt = con.prepareStatement(query);
            pstmt.setInt(1, menuCode);

            result = pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCTemplate.close(pstmt);
        }

        return result;
    }

    /**
     * 전체 카테고리 목록을 조회합니다 (카테고리 필터 및 등록/수정 드롭다운용).
     */
    public List<com.uahan.menu.model.dto.CategoryDTO> selectAllCategories(Connection con) {
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        List<com.uahan.menu.model.dto.CategoryDTO> categoryList = null;

        String query = prop.getProperty("selectAllCategories");

        try {
            pstmt = con.prepareStatement(query);
            rset = pstmt.executeQuery();

            categoryList = new ArrayList<>();

            while (rset.next()) {
                com.uahan.menu.model.dto.CategoryDTO category = new com.uahan.menu.model.dto.CategoryDTO();
                category.setCategoryCode(rset.getInt("category_code"));
                category.setCategoryName(rset.getString("category_name"));
                category.setRefCategoryCode(rset.getObject("ref_category_code", Integer.class));

                categoryList.add(category);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCTemplate.close(rset);
            JDBCTemplate.close(pstmt);
        }

        return categoryList;
    }

}
