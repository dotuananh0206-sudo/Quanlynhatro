# 🏢 TroManager - Phần Mềm Quản Lý Phòng Trọ Toàn Diện (JavaFX)

Ứng dụng Desktop JavaFX quản lý nhà trọ và phòng cho thuê **TroManager**, được hoàn thiện toàn bộ **10 màn hình chức năng** theo đúng chuẩn thiết kế UI/UX hiện đại, thống nhất phong cách (Modern SaaS Dashboard), màu sắc sinh động, vector icon sắc nét, dữ liệu mẫu (mock data) phong phú và đầy đủ tương tác nút bấm.

---

## 🧭 Tổng Quan 10 Tab Chức Năng Hoàn Chỉnh

| STT | Tab Menu | Chức năng chính |
| :---: | :--- | :--- |
| 1 | **Dashboard** (Tổng quan) | 5 thẻ KPI doanh thu & phòng, Biểu đồ cột 6 tháng, Biểu đồ Donut tỷ lệ lấp đầy (87.5%), Bảng hóa đơn, hợp đồng sắp hạn & công việc khẩn cấp. |
| 2 | **Khu trọ & Phòng** | Bộ lọc khu trọ, Lưới thẻ phòng phân theo TẦNG 1 - TẦNG 3, Panel chi tiết phòng tương tác trực tiếp khi click (giá thuê, điện nước, tiện nghi, tạo hóa đơn). |
| 3 | **Khách thuê** | 4 thẻ thống kê tạm trú & khách mới, tìm kiếm theo Tên/SĐT/CCCD, bộ lọc tạm trú, Panel hồ sơ chi tiết khách, khai báo tạm trú & thủ tục trả phòng. |
| 4 | **Hợp đồng** | Quản lý hợp đồng thuê, thẻ đếm hạn < 30 ngày, tìm kiếm & lọc trạng thái, Panel điều khoản hợp đồng, nút Gia hạn, In hợp đồng PDF, Thanh lý. |
| 5 | **Hóa đơn** | Quản lý thu chi từng phòng, tính tiền phòng + điện + nước + dịch vụ, bộ lọc trạng thái thanh toán, Lập hóa đơn hàng loạt & In phiếu thu PDF. |
| 6 | **Tài sản** | Quản lý 184 thiết bị & trang thiết bị (Điều hòa, tủ lạnh, nóng lạnh, nội thất...), lọc theo danh mục, báo bảo trì & khấu hao thanh lý. |
| 7 | **Công việc** | Bảng công việc bảo trì dạng Kanban 3 cột: **Cần xử lý (To-Do)**, **Đang xử lý (In Progress)**, **Đã xong (Done)** kèm mức độ ưu tiên & phân công. |
| 8 | **Giao tiếp** | Kênh chat trực tiếp với từng phòng trọ, gửi tin nhắn trao đổi, nút **Gửi thông báo toàn tòa nhà (Broadcast)** và gọi điện nhanh. |
| 9 | **Báo cáo** | Thống kê doanh thu, chi phí vận hành (EVN, nước, bảo trì), lợi nhuận ròng, tỷ suất ROI, biểu đồ cơ cấu nguồn thu và bảng dòng tiền năm 2024. |
| 10 | **Cài đặt** | Cấu hình thông tin khu trọ & chủ hộ, biểu giá điện nước (3.500đ/kWh, 25.000đ/m³), tài khoản ngân hàng nhận tiền QR Code, chu kỳ chốt số. |

---

## 🚀 Hướng Dẫn Chạy Thử Ứng Dụng

### Thư mục dự án:
```text
C:\Users\admin\Downloads\tromanager-javafx
```

### Cách 1: Chạy bằng 1 click chuột (Khuyên dùng trên Windows)
1. Mở thư mục `C:\Users\admin\Downloads\tromanager-javafx`.
2. **Nhấp đúp chuột vào file `run.bat`**.
3. Ứng dụng sẽ tự động tải thư viện và mở cửa sổ TroManager!

---

### Cách 2: Chạy bằng dòng lệnh Terminal / PowerShell
Mở PowerShell hoặc CMD tại thư mục dự án và chạy:
```powershell
cd C:\Users\admin\Downloads\tromanager-javafx
.\mvnw.cmd javafx:run
```

---

### Cách 3: Mở trong IDE (IntelliJ IDEA / VS Code / Eclipse)
- Mở thư mục `tromanager-javafx` trong IDE.
- Mở file `src/main/java/com/tromanager/MainApp.java` và chọn **Run** (▶️).
