<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>
        <html>

        <head>
            <title>배달의 민족 - 메뉴 관리</title>
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style.css">

            <%-- Fallback Icon Logic (Inline for simplicity or move to backend completely) --%>
                <%-- Using JSTL Logic in logic bean is better, but here we assume icon is handled effectively or use
                    simple map --%>
        </head>

        <body>

            <div class="container">
                <header>
                    <a href="${pageContext.request.contextPath}/" class="header-btn">‹ 홈</a>
                    <h1>메뉴 관리</h1>
                    <div style="width: 30px;"></div> <!-- Spacer -->
                </header>

                <div class="sort-controls">
                    <button class="sort-btn" id="sortName" onclick="toggleSort('name')">이름순</button>
                    <button class="sort-btn" id="sortPrice" onclick="toggleSort('price')">가격순</button>
                </div>

                <div class="menu-list" id="menuListContainer">
                    <jsp:include page="list_content.jsp" />
                </div>

                <!-- Floating Action Button for Registration -->
                <button onclick="openRegistModal()" class="fab" style="border:none;">+</button>

                <footer class="site-footer">
                    <div class="footer-content">
                        <p class="footer-team">1팀 | 팀원: 정진호 윤성원 정병진 최현지</p>
                        <p class="footer-copy">Copyright © Baemin Corp. All rights reserved.</p>
                    </div>
                </footer>
            </div>

            <!-- Toast Notification -->
            <div id="toastContainer" class="toast-container">
                <div id="toast" class="toast">메시지</div>
            </div>

            <!-- Detail Modal -->
            <div id="detailModal" class="modal-overlay">
                <div class="modal-content">
                    <button class="btn-close" onclick="closeDetailModal()">×</button>
                    <h2 class="modal-title" id="modalTitle">메뉴 상세</h2>

                    <div class="modal-body">
                        <p><span class="modal-label">메뉴명</span> <span class="modal-value" id="detailName"></span></p>
                        <p><span class="modal-label">가격</span> <span class="modal-value" id="detailPrice"></span></p>
                        <p><span class="modal-label">카테고리</span> <span class="modal-value" id="detailCategory"></span>
                        </p>
                        <p><span class="modal-label">상태</span> <span class="modal-value" id="detailStatus"></span></p>
                    </div>

                    <div class="modal-actions">
                        <button class="btn btn-primary" id="btnToUpdate">수정</button>
                        <button class="btn btn-danger" onclick="openDeleteModal()">삭제</button>
                    </div>
                </div>
            </div>

            <!-- Registration Modal -->
            <div id="registModal" class="modal-overlay">
                <div class="modal-content">
                    <button class="btn-close" onclick="closeRegistModal()">×</button>
                    <h2 class="modal-title">메뉴 등록</h2>

                    <form id="registForm">
                        <div class="form-group">
                            <label class="form-label">메뉴명</label>
                            <input type="text" name="menuName" class="form-input" placeholder="예: 맛있는 치킨" required>
                        </div>

                        <div class="form-group">
                            <label class="form-label">가격</label>
                            <input type="number" name="menuPrice" class="form-input" placeholder="10000" required>
                        </div>

                        <div class="form-group">
                            <label class="form-label">카테고리</label>
                            <select name="categoryCode" class="form-select" required>
                                <c:forEach var="category" items="${categoryList}">
                                    <option value="${category.categoryCode}">${category.categoryName}</option>
                                </c:forEach>
                            </select>
                        </div>

                        <div class="form-group">
                            <label class="form-label">판매 상태</label>
                            <select name="orderableStatus" class="form-select">
                                <option value="Y">주문 가능</option>
                                <option value="N">품절</option>
                            </select>
                        </div>

                        <div class="modal-actions">
                            <button type="button" class="btn btn-secondary" onclick="closeRegistModal()">취소</button>
                            <button type="submit" class="btn btn-primary">등록하기</button>
                        </div>
                    </form>
                </div>
            </div>

            <!-- Update Modal -->
            <div id="updateModal" class="modal-overlay">
                <div class="modal-content">
                    <button class="btn-close" onclick="closeUpdateModal()">×</button>
                    <h2 class="modal-title">메뉴 수정</h2>

                    <form id="updateForm">
                        <input type="hidden" name="menuCode" id="updateMenuCode">

                        <div class="form-group">
                            <label class="form-label">메뉴명</label>
                            <input type="text" name="menuName" id="updateMenuName" class="form-input" required>
                        </div>

                        <div class="form-group">
                            <label class="form-label">가격</label>
                            <input type="number" name="menuPrice" id="updateMenuPrice" class="form-input" required>
                        </div>

                        <div class="form-group">
                            <label class="form-label">카테고리</label>
                            <select name="categoryCode" id="updateCategoryCode" class="form-select">
                                <c:forEach var="category" items="${categoryList}">
                                    <option value="${category.categoryCode}">${category.categoryName}</option>
                                </c:forEach>
                            </select>
                        </div>

                        <div class="form-group">
                            <label class="form-label">판매 상태</label>
                            <select name="orderableStatus" id="updateOrderableStatus" class="form-select">
                                <option value="Y">주문 가능</option>
                                <option value="N">품절</option>
                            </select>
                        </div>

                        <div class="modal-actions">
                            <button type="button" class="btn btn-secondary" onclick="closeUpdateModal()">취소</button>
                            <button type="submit" class="btn btn-primary">수정 완료</button>
                        </div>
                    </form>
                </div>
            </div>

            <!-- Delete Confirmation Modal -->
            <div id="deleteModal" class="modal-overlay delete-modal">
                <div class="modal-content">
                    <button class="btn-close" onclick="closeDeleteModal()">×</button>
                    <h2 class="modal-title">메뉴 삭제</h2>

                    <div class="modal-body" style="text-align: center; display: block;">
                        <p style="justify-content: center; margin-bottom: 20px;">정말 삭제하시겠습니까?<br>삭제된 메뉴는 복구할 수 없습니다.</p>
                    </div>

                    <div class="modal-actions">
                        <button class="btn btn-secondary" onclick="closeDeleteModal()">취소</button>
                        <button class="btn btn-primary" onclick="confirmDelete()">삭제하기</button>
                    </div>
                </div>
            </div>

            <script>
                // --- Notification Toast ---
                function showToast(message, type) {
                    const toast = document.getElementById('toast');
                    toast.innerText = message;
                    toast.className = 'toast show ' + type;

                    setTimeout(function () {
                        toast.className = toast.className.replace('show', '');
                    }, 3000); // Hide after 3 seconds
                }

                // --- Common AJAX Handler ---
                function sendAjax(url, formData, successMsg, failMsg, onSuccess) {
                    const params = new URLSearchParams(formData);

                    fetch(url, {
                        method: 'POST',
                        body: params,
                        headers: {
                            'X-Requested-With': 'XMLHttpRequest',
                            'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                        }
                    })
                        .then(response => response.text())
                        .then(result => {
                            if (result.trim() === 'success') {
                                showToast(successMsg, 'success');
                                closeAllModals();
                                refreshList();
                                if (onSuccess) onSuccess();
                            } else {
                                showToast(failMsg, 'error');
                            }
                        })
                        .catch(err => {
                            console.error('Error:', err);
                            showToast('오류가 발생했습니다.', 'error');
                        });
                }

                // --- Refresh List ---
                function refreshList() {
                    const sort = currentSortState;
                    const url = '${pageContext.request.contextPath}/menu/list?sort=' + sort;

                    fetch(url, { headers: { 'X-Requested-With': 'XMLHttpRequest' } })
                        .then(response => response.text())
                        .then(html => {
                            document.getElementById('menuListContainer').innerHTML = html;
                        });
                }

                function closeAllModals() {
                    document.getElementById('detailModal').style.display = 'none';
                    document.getElementById('registModal').style.display = 'none';
                    document.getElementById('updateModal').style.display = 'none';
                    document.getElementById('deleteModal').style.display = 'none';
                    currentMenuCode = null;

                    // Clear Update Form State when fully exiting
                    clearUpdateFormState();
                }

                function clearUpdateFormState() {
                    document.getElementById('updateMenuCode').value = '';
                    document.getElementById('updateForm').reset();
                }

                // --- Global Events (ESC Key) ---
                document.addEventListener('keydown', function (event) {
                    if (event.key === 'Escape') {
                        closeAllModals();
                    }
                });

                // --- State ---
                let currentMenuCode = null;
                let currentSortState = '';

                // --- Regist Modal ---
                function openRegistModal() {
                    document.getElementById('registModal').style.display = 'flex';
                    // Focus first input if empty
                    const nameInput = document.querySelector('#registForm input[name="menuName"]');
                    if (!nameInput.value) setTimeout(() => nameInput.focus(), 100);
                }
                function closeRegistModal() {
                    document.getElementById('registModal').style.display = 'none';
                }

                document.getElementById('registForm').onsubmit = function (e) {
                    e.preventDefault();
                    if (!this.checkValidity()) {
                        this.reportValidity();
                        return;
                    }
                    const formData = new FormData(this);
                    // Pass callback to reset form ONLY on success
                    sendAjax('${pageContext.request.contextPath}/menu/regist', formData, '메뉴가 등록되었습니다.', '메뉴 등록에 실패했습니다.', () => {
                        document.getElementById('registForm').reset();
                    });
                };

                // --- Detail Modal ---
                function openModal(code, name, price, categoryName, categoryCode, status) {
                    currentMenuCode = code;

                    document.getElementById('detailName').innerText = name;
                    document.getElementById('detailPrice').innerText = price + '원';
                    document.getElementById('detailCategory').innerText = categoryName;
                    document.getElementById('detailStatus').innerText = status === 'Y' ? '주문가능' : '품절';

                    document.getElementById('btnToUpdate').onclick = function () {
                        openUpdateModal(code, name, price, categoryCode, status);
                    };

                    document.getElementById('detailModal').style.display = 'flex';
                }
                function closeDetailModal() {
                    document.getElementById('detailModal').style.display = 'none';
                    clearUpdateFormState(); // Leaving Detail clears Update state
                }

                // --- Update Modal ---
                function openUpdateModal(code, name, price, categoryCode, status) {
                    // Only populate if it's a new item (code mismatch)
                    const currentUpCode = document.getElementById('updateMenuCode').value;

                    if (currentUpCode != code) {
                        document.getElementById('updateMenuCode').value = code;
                        document.getElementById('updateMenuName').value = name;
                        document.getElementById('updateMenuPrice').value = price;
                        document.getElementById('updateCategoryCode').value = categoryCode;
                        document.getElementById('updateOrderableStatus').value = status;
                    }

                    document.getElementById('updateModal').style.display = 'flex';
                    document.getElementById('detailModal').style.display = 'none';

                    setTimeout(() => document.getElementById('updateMenuName').focus(), 100);
                }
                function closeUpdateModal() {
                    document.getElementById('updateModal').style.display = 'none';
                    document.getElementById('detailModal').style.display = 'flex';
                }

                document.getElementById('updateForm').onsubmit = function (e) {
                    e.preventDefault();
                    if (!this.checkValidity()) {
                        this.reportValidity();
                        return;
                    }
                    const formData = new FormData(this);
                    sendAjax('${pageContext.request.contextPath}/menu/update', formData, '메뉴가 수정되었습니다.', '메뉴 수정에 실패했습니다.');
                };

                // --- Delete Modal ---
                function openDeleteModal() {
                    document.getElementById('deleteModal').style.display = 'flex';
                    document.getElementById('detailModal').style.display = 'none';
                }
                function closeDeleteModal() {
                    document.getElementById('deleteModal').style.display = 'none';
                    document.getElementById('detailModal').style.display = 'flex'; // Cancel delete returns to detail
                }
                function confirmDelete() {
                    if (!currentMenuCode) return;
                    const formData = new FormData();
                    formData.append('menuCode', currentMenuCode);
                    sendAjax('${pageContext.request.contextPath}/menu/delete', formData, '메뉴가 삭제되었습니다.', '메뉴 삭제에 실패했습니다.');
                }


                // --- Sorting ---
                function toggleSort(type) {
                    let newSort = '';
                    if (type === 'name') {
                        if (currentSortState === 'name_asc') newSort = 'name_desc';
                        else if (currentSortState === 'name_desc') newSort = '';
                        else newSort = 'name_asc';
                    } else if (type === 'price') {
                        if (currentSortState === 'price_asc') newSort = 'price_desc';
                        else if (currentSortState === 'price_desc') newSort = '';
                        else newSort = 'price_asc';
                    }
                    currentSortState = newSort;
                    updateSortButtons(newSort);
                    refreshList();

                    const newUrl = new URL(window.location);
                    if (newSort) newUrl.searchParams.set('sort', newSort);
                    else newUrl.searchParams.delete('sort');
                    window.history.pushState({}, '', newUrl);
                }

                function updateSortButtons(sort) {
                    document.getElementById('sortName').className = 'sort-btn';
                    document.getElementById('sortPrice').className = 'sort-btn';
                    if (!sort) return;

                    if (sort.startsWith('name')) {
                        const btn = document.getElementById('sortName');
                        btn.className = 'sort-btn active';
                        if (sort.endsWith('asc')) btn.classList.add('asc');
                        if (sort.endsWith('desc')) btn.classList.add('desc');
                    } else if (sort.startsWith('price')) {
                        const btn = document.getElementById('sortPrice');
                        btn.className = 'sort-btn active';
                        if (sort.endsWith('asc')) btn.classList.add('asc');
                        if (sort.endsWith('desc')) btn.classList.add('desc');
                    }
                }

                // --- Init ---
                window.onload = function () {
                    const urlParams = new URLSearchParams(window.location.search);
                    currentSortState = urlParams.get('sort') || '';
                    updateSortButtons(currentSortState);
                };

                // --- Screen Click Close Logic (Improved) ---
                let isMouseDownOnOverlay = false;

                window.onmousedown = function (event) {
                    // Start tracking if the mouse was pressed ON the overlay
                    if (event.target.classList.contains('modal-overlay')) {
                        isMouseDownOnOverlay = true;
                    } else {
                        isMouseDownOnOverlay = false;
                    }
                };

                window.onclick = function (event) {
                    // Only close if the mouse was pressed down ON the overlay (not dragged from inside)
                    if (!isMouseDownOnOverlay) return;

                    if (event.target == document.getElementById('detailModal')) closeDetailModal();
                    if (event.target == document.getElementById('registModal')) closeRegistModal();
                    if (event.target == document.getElementById('updateModal')) closeUpdateModal();
                    if (event.target == document.getElementById('deleteModal')) closeDeleteModal();

                    isMouseDownOnOverlay = false; // Reset
                }

            </script>
        </body>

        </html>