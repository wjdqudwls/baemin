package com.uahan.menu.controller;

import com.uahan.menu.model.dto.MenuDTO;
import com.uahan.menu.model.dto.CategoryDTO;
import com.uahan.menu.model.service.MenuService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/menu/*")
public class MenuController extends HttpServlet {

    private MenuService menuService;

    @Override
    public void init() throws ServletException {
        menuService = new MenuService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();

        // 메뉴 목록 조회 (기본 경로 또는 /list)
        if (pathInfo == null || "/list".equals(pathInfo)) {
            // 브라우저에서 보낸 검색 및 필터 파라미터 추출
            String searchQuery = req.getParameter("searchQuery");
            String categoryCodeStr = req.getParameter("categoryCode");
            String excludeSoldOutStr = req.getParameter("excludeSoldOut");
            String nameSort = req.getParameter("nameSort");
            String priceSort = req.getParameter("priceSort");

            // 카테고리 코드를 정수형으로 변환 (입력이 없을 경우 null 유지)
            Integer categoryCode = null;
            if (categoryCodeStr != null && !categoryCodeStr.trim().isEmpty()) {
                try {
                    categoryCode = Integer.parseInt(categoryCodeStr);
                } catch (NumberFormatException e) {
                    // 잘못된 형식의 코드가 들어올 경우 무시
                }
            }

            // '품절 제외' 체크박스 상태 변환 (문자열 -> 불리언)
            boolean excludeSoldOut = Boolean.parseBoolean(excludeSoldOutStr);

            // 고도화된 검색 메서드를 사용하여 DB에서 데이터 조회
            List<MenuDTO> menuList = menuService.searchMenus(searchQuery, categoryCode, excludeSoldOut, nameSort,
                    priceSort);
            // 드롭다운 메뉴용 전체 카테고리 목록 조회
            List<CategoryDTO> categoryList = menuService.selectAllCategories();

            // JSP에서 사용할 수 있도록 데이터를 request 영역에 저장
            req.setAttribute("menuList", menuList);
            req.setAttribute("categoryList", categoryList);

            // AJAX 요청인지 확인 (X-Requested-With 헤더 검사)
            String ajaxHeader = req.getHeader("X-Requested-With");
            if ("XMLHttpRequest".equals(ajaxHeader)) {
                // AJAX 요청인 경우 목록 조각(HTML Fragment)만 반환
                req.getRequestDispatcher("/WEB-INF/views/menu/list_content.jsp").forward(req, resp);
            } else {
                // 일반 요청인 경우 전체 리스트 페이지 반환
                req.getRequestDispatcher("/WEB-INF/views/menu/list.jsp").forward(req, resp);
            }
        } else {
            // 등록, 수정, 상세 모달을 사용하므로, 잘못된 GET 요청은 목록 페이지로 리다이렉트
            resp.sendRedirect(req.getContextPath() + "/menu/list");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        // 한글 깨짐 방지 설정
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("text/plain;charset=UTF-8"); // AJAX 응답용 설정

        PrintWriter out = resp.getWriter();
        int result = 0;

        try {
            if ("/regist".equals(pathInfo)) {
                // 신규 메뉴 등록 처리
                String name = req.getParameter("menuName");
                int price = Integer.parseInt(req.getParameter("menuPrice"));
                int categoryCode = Integer.parseInt(req.getParameter("categoryCode"));
                String status = req.getParameter("orderableStatus");

                MenuDTO menu = new MenuDTO();
                menu.setMenuName(name);
                menu.setMenuPrice(price);
                menu.setCategoryCode(categoryCode);
                menu.setOrderableStatus(status);

                result = menuService.registMenu(menu);

            } else if ("/update".equals(pathInfo)) {
                // 기존 메뉴 정보 수정 처리
                int code = Integer.parseInt(req.getParameter("menuCode"));
                String name = req.getParameter("menuName");
                int price = Integer.parseInt(req.getParameter("menuPrice"));
                int categoryCode = Integer.parseInt(req.getParameter("categoryCode"));
                String status = req.getParameter("orderableStatus");

                MenuDTO menu = new MenuDTO(code, name, price, categoryCode, status);

                result = menuService.modifyMenu(menu);

            } else if ("/delete".equals(pathInfo)) {
                // 메뉴 삭제 처리
                int code = Integer.parseInt(req.getParameter("menuCode"));
                result = menuService.deleteMenu(code);
            }

            // 처리 결과에 따른 응답 전송
            if (result > 0) {
                out.print("success"); // 성공 시 "success" 문자열 반환
            } else {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("fail");
            }

        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("error");
        }
    }
}
