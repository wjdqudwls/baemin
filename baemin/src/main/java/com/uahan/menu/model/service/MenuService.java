package com.uahan.menu.model.service;

import com.uahan.common.JDBCTemplate;
import com.uahan.menu.model.dao.MenuDAO;
import com.uahan.menu.model.dto.CategoryDTO;
import com.uahan.menu.model.dto.MenuDTO;

import java.sql.Connection;
import java.util.List;

public class MenuService {

    private final MenuDAO menuDAO;

    public MenuService() {
        menuDAO = new MenuDAO();
    }

    /**
     * 필터링된 메뉴 목록을 조회하는 비즈니스 로직입니다.
     */
    public List<MenuDTO> searchMenus(String searchQuery, Integer categoryCode, boolean excludeSoldOut, String nameSort,
            String priceSort) {
        Connection con = JDBCTemplate.getConnection();
        List<MenuDTO> menuList = menuDAO.selectMenusWithFilter(con, searchQuery, categoryCode, excludeSoldOut, nameSort,
                priceSort);
        JDBCTemplate.close(con);
        return menuList;
    }

    /**
     * 특정 메뉴 상세 정보를 조회합니다.
     */
    public MenuDTO selectMenuById(int menuCode) {
        Connection con = JDBCTemplate.getConnection();
        MenuDTO menu = menuDAO.selectMenuById(con, menuCode);
        JDBCTemplate.close(con);
        return menu;
    }

    /**
     * 새로운 메뉴를 등록하고 트랜잭션을 처리합니다.
     */
    public int registMenu(MenuDTO menu) {
        Connection con = JDBCTemplate.getConnection();
        int result = menuDAO.insertMenu(con, menu);

        // 결과에 따라 커밋 또는 롤백 수행 (트랜잭션 관리)
        if (result > 0) {
            JDBCTemplate.commit(con);
        } else {
            JDBCTemplate.rollback(con);
        }
        JDBCTemplate.close(con);

        return result;
    }

    /**
     * 메뉴 정보를 수정하고 트랜잭션을 처리합니다.
     */
    public int modifyMenu(MenuDTO menu) {
        Connection con = JDBCTemplate.getConnection();
        int result = menuDAO.updateMenu(con, menu);

        if (result > 0) {
            JDBCTemplate.commit(con);
        } else {
            JDBCTemplate.rollback(con);
        }
        JDBCTemplate.close(con);

        return result;
    }

    /**
     * 메뉴를 삭제하고 트랜잭션을 처리합니다.
     */
    public int deleteMenu(int menuCode) {
        Connection con = JDBCTemplate.getConnection();
        int result = menuDAO.deleteMenu(con, menuCode);

        if (result > 0) {
            JDBCTemplate.commit(con);
        } else {
            JDBCTemplate.rollback(con);
        }
        JDBCTemplate.close(con);

        return result;
    }

    /**
     * 전체 카테고리 목록을 조회합니다.
     */
    public List<CategoryDTO> selectAllCategories() {
        Connection con = JDBCTemplate.getConnection();
        List<CategoryDTO> categoryList = menuDAO.selectAllCategories(con);
        JDBCTemplate.close(con);
        return categoryList;
    }
}
