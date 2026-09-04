package com.tromanager.service;

import com.tromanager.model.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class MockDataService {
    private static MockDataService instance;

    private List<Room> rooms;
    private List<Tenant> tenants;
    private List<Contract> contracts;
    private List<Invoice> invoices;
    private List<Asset> assets;
    private List<TaskItem> tasks;
    private List<RevenueData> revenueData;
    private List<MessageItem> messages;

    private MockDataService() {
        initData();
    }

    public static synchronized MockDataService getInstance() {
        if (instance == null) {
            instance = new MockDataService();
        }
        return instance;
    }

    private void initData() {
        // Rooms
        rooms = new ArrayList<>();
        
        // Floor 1
        rooms.add(new Room("1", "P.101", 1, "Khu trọ Hòa Bình", RoomStatus.RENTED,
                "Nguyễn Văn An", "0912 345 678", "01/03/2024", "01/03/2025",
                3500000L, 3500L, 25000L, 25,
                Arrays.asList("Máy lạnh", "Wifi", "WC riêng", "Tủ lạnh")));

        rooms.add(new Room("2", "P.102", 1, "Khu trọ Hòa Bình", RoomStatus.RENTED,
                "Trần Thị Lan", "0987 654 321", "15/12/2023", "15/12/2025",
                3500000L, 3500L, 25000L, 25,
                Arrays.asList("Máy lạnh", "Wifi", "WC riêng")));

        rooms.add(new Room("3", "P.103", 1, "Khu trọ Hòa Bình", RoomStatus.VACANT,
                "Chưa có khách", "", "", "",
                3200000L, 3500L, 25000L, 25,
                Arrays.asList("Máy lạnh", "WC riêng")));

        rooms.add(new Room("4", "P.104", 1, "Khu trọ Hòa Bình", RoomStatus.RENTED,
                "Bùi Minh Triết", "0933 112 233", "15/09/2023", "15/09/2024",
                3600000L, 3500L, 25000L, 28,
                Arrays.asList("Máy lạnh", "Wifi")));

        // Floor 2
        rooms.add(new Room("5", "P.201", 2, "Khu trọ Hòa Bình", RoomStatus.RENTED,
                "Lê Minh Hoàng", "0918 223 344", "28/08/2023", "28/08/2025",
                3500000L, 3500L, 25000L, 25,
                Arrays.asList("Máy lạnh", "Wifi", "WC riêng")));

        rooms.add(new Room("6", "P.202", 2, "Khu trọ Hòa Bình", RoomStatus.EXPIRING_SOON,
                "Phạm Thị Thùy", "0977 445 566", "10/11/2023", "10/11/2024",
                3500000L, 3500L, 25000L, 25,
                Arrays.asList("Máy lạnh", "Wifi", "WC riêng")));

        rooms.add(new Room("7", "P.203", 2, "Khu trọ Hòa Bình", RoomStatus.RENTED,
                "Vũ Hoàng Long", "0909 887 766", "28/09/2023", "28/09/2024",
                3500000L, 3500L, 25000L, 25,
                Arrays.asList("Máy lạnh", "Wifi")));

        rooms.add(new Room("8", "P.204", 2, "Khu trọ Hòa Bình", RoomStatus.RENTED,
                "Lê Tuấn Kiệt", "0966 332 211", "20/02/2024", "20/02/2025",
                3500000L, 3500L, 25000L, 25,
                Arrays.asList("Máy lạnh", "WC riêng")));

        // Floor 3
        rooms.add(new Room("9", "P.301", 3, "Khu trọ Hòa Bình", RoomStatus.RENTED,
                "Trịnh Quốc Bảo", "0944 556 677", "25/10/2023", "25/10/2024",
                3700000L, 3500L, 25000L, 30,
                Arrays.asList("Máy lạnh", "Wifi", "WC riêng")));

        rooms.add(new Room("10", "P.302", 3, "Khu trọ Hòa Bình", RoomStatus.RENTED,
                "Đỗ Thị Thắm", "0938 123 789", "10/10/2023", "10/10/2024",
                3700000L, 3500L, 25000L, 30,
                Arrays.asList("Máy lạnh", "Wifi", "WC riêng")));

        rooms.add(new Room("11", "P.303", 3, "Khu trọ Hòa Bình", RoomStatus.VACANT,
                "Chưa có khách", "", "", "",
                3300000L, 3500L, 25000L, 25,
                Arrays.asList("Máy lạnh", "WC riêng")));

        rooms.add(new Room("12", "P.304", 3, "Khu trọ Hòa Bình", RoomStatus.RENTED,
                "Phạm Văn Hùng", "0915 998 877", "25/08/2023", "25/08/2025",
                3700000L, 3500L, 25000L, 30,
                Arrays.asList("Máy lạnh", "Wifi", "WC riêng")));

        // Tenants
        tenants = new ArrayList<>(Arrays.asList(
                new Tenant("KT01", "Nguyễn Văn An", "0912 345 678", "P.101", "001200012345", "Hà Nội", "01/03/2024", 3500000L, true),
                new Tenant("KT02", "Trần Thị Lan", "0987 654 321", "P.102", "034201098765", "Nam Định", "15/12/2023", 3500000L, true),
                new Tenant("KT03", "Bùi Minh Triết", "0933 112 233", "P.104", "079202054321", "Thái Bình", "15/09/2023", 3600000L, false),
                new Tenant("KT04", "Lê Minh Hoàng", "0918 223 344", "P.201", "025200087654", "Hải Phòng", "28/08/2023", 3500000L, true),
                new Tenant("KT05", "Phạm Thị Thùy", "0977 445 566", "P.202", "019201043210", "Nghệ An", "10/11/2023", 3500000L, true),
                new Tenant("KT06", "Vũ Hoàng Long", "0909 887 766", "P.203", "040203011223", "Bắc Ninh", "28/09/2023", 3500000L, true),
                new Tenant("KT07", "Lê Tuấn Kiệt", "0966 332 211", "P.204", "036200065432", "Thanh Hóa", "20/02/2024", 3500000L, false),
                new Tenant("KT08", "Trịnh Quốc Bảo", "0944 556 677", "P.301", "001201099887", "Hà Nội", "25/10/2023", 3700000L, true),
                new Tenant("KT09", "Đỗ Thị Thắm", "0938 123 789", "P.302", "022202033445", "Hải Dương", "10/10/2023", 3700000L, true),
                new Tenant("KT10", "Phạm Văn Hùng", "0915 998 877", "P.304", "017200077889", "Vĩnh Phúc", "25/08/2023", 3700000L, true)
        ));

        // Contracts
        contracts = new ArrayList<>(Arrays.asList(
                new Contract("HD-2024-101", "P.101", "Nguyễn Văn An", "0912 345 678", "01/03/2024", "01/03/2025", 3500000L, 3500000L, 187, Contract.Status.ACTIVE),
                new Contract("HD-2023-102", "P.102", "Trần Thị Lan", "0987 654 321", "15/12/2023", "15/12/2025", 3500000L, 3500000L, 476, Contract.Status.ACTIVE),
                new Contract("HD-2023-104", "P.104", "Bùi Minh Triết", "0933 112 233", "15/09/2023", "15/09/2024", 3600000L, 3600000L, 18, Contract.Status.EXPIRING),
                new Contract("HD-2023-201", "P.201", "Lê Minh Hoàng", "0918 223 344", "28/08/2023", "28/08/2025", 3500000L, 3500000L, 367, Contract.Status.ACTIVE),
                new Contract("HD-2023-202", "P.202", "Phạm Thị Thùy", "0977 445 566", "10/11/2023", "10/11/2024", 3500000L, 3500000L, 76, Contract.Status.ACTIVE),
                new Contract("HD-2023-203", "P.203", "Vũ Hoàng Long", "0909 887 766", "28/09/2023", "28/09/2024", 3500000L, 3500000L, 31, Contract.Status.EXPIRING),
                new Contract("HD-2024-204", "P.204", "Lê Tuấn Kiệt", "0966 332 211", "20/02/2024", "20/02/2025", 3500000L, 3500000L, 178, Contract.Status.ACTIVE),
                new Contract("HD-2023-301", "P.301", "Trịnh Quốc Bảo", "0944 556 677", "25/10/2023", "25/10/2024", 3700000L, 3700000L, 58, Contract.Status.EXPIRING),
                new Contract("HD-2023-302", "P.302", "Đỗ Thị Thắm", "0938 123 789", "10/10/2023", "10/10/2024", 3700000L, 3700000L, 43, Contract.Status.EXPIRING),
                new Contract("HD-2023-304", "P.304", "Phạm Văn Hùng", "0915 998 877", "25/08/2023", "25/08/2025", 3700000L, 3700000L, 364, Contract.Status.ACTIVE)
        ));

        // Invoices
        invoices = new ArrayList<>(Arrays.asList(
                new Invoice("INV-08-101", "P.101", "Nguyễn Văn An", "Tháng 08/2024", 3500000L, 280000L, 100000L, 50000L, "20/08/2024", Invoice.InvoiceStatus.PAID),
                new Invoice("INV-08-102", "P.102", "Trần Thị Lan", "Tháng 08/2024", 3500000L, 350000L, 100000L, 50000L, "Hôm nay", Invoice.InvoiceStatus.UNPAID),
                new Invoice("INV-08-104", "P.104", "Bùi Minh Triết", "Tháng 08/2024", 3600000L, 310000L, 120000L, 50000L, "30/08/2024", Invoice.InvoiceStatus.UNPAID),
                new Invoice("INV-08-201", "P.201", "Lê Minh Hoàng", "Tháng 08/2024", 3200000L, 240000L, 80000L, 50000L, "28/08/2024", Invoice.InvoiceStatus.PAID),
                new Invoice("INV-08-202", "P.202", "Phạm Thị Thùy", "Tháng 08/2024", 3500000L, 390000L, 100000L, 50000L, "25/08/2024", Invoice.InvoiceStatus.DUE_SOON),
                new Invoice("INV-08-203", "P.203", "Vũ Hoàng Long", "Tháng 08/2024", 3500000L, 420000L, 100000L, 50000L, "28/08/2024", Invoice.InvoiceStatus.PAID),
                new Invoice("INV-08-301", "P.301", "Trịnh Quốc Bảo", "Tháng 08/2024", 3700000L, 360000L, 120000L, 50000L, "30/08/2024", Invoice.InvoiceStatus.DUE_SOON),
                new Invoice("INV-08-304", "P.304", "Phạm Văn Hùng", "Tháng 08/2024", 3700000L, 450000L, 120000L, 50000L, "25/08/2024", Invoice.InvoiceStatus.DUE_SOON),
                new Invoice("INV-08-405", "P.405", "Hoàng Văn Nam", "Tháng 08/2024", 4500000L, 380000L, 120000L, 0L, "30/08/2024", Invoice.InvoiceStatus.UNPAID)
        ));

        // Assets
        assets = new ArrayList<>(Arrays.asList(
                new Asset("TS-001", "Điều hòa Daikin Inverter 9000BTU", "P.101", "Điện lạnh", 1, 8500000L, "01/03/2024", Asset.AssetStatus.GOOD),
                new Asset("TS-002", "Tủ lạnh mini Aqua 90L", "P.101", "Điện gia dụng", 1, 2800000L, "01/03/2024", Asset.AssetStatus.GOOD),
                new Asset("TS-003", "Bình nóng lạnh Ariston 20L", "P.102", "Điện lạnh", 1, 2500000L, "15/12/2023", Asset.AssetStatus.GOOD),
                new Asset("TS-004", "Điều hòa Panasonic 12000BTU", "P.305", "Điện lạnh", 1, 9200000L, "10/05/2022", Asset.AssetStatus.MAINTENANCE),
                new Asset("TS-005", "Giường gỗ sồi 1m6 x 2m", "P.103", "Nội thất", 1, 3500000L, "01/06/2023", Asset.AssetStatus.GOOD),
                new Asset("TS-006", "Bàn làm việc + ghế xoay", "P.201", "Nội thất", 1, 1200000L, "28/08/2023", Asset.AssetStatus.GOOD),
                new Asset("TS-007", "Vòi hoa sen inox 304", "P.102", "Thiết bị vệ sinh", 1, 450000L, "15/12/2023", Asset.AssetStatus.BROKEN),
                new Asset("TS-008", "Router Wifi TP-Link Gigabit Tầng 2", "Khu vực chung", "Mạng viễn thông", 1, 1100000L, "01/01/2024", Asset.AssetStatus.GOOD)
        ));

        // Tasks
        tasks = new ArrayList<>(Arrays.asList(
                new TaskItem("Sửa vòi nước nhà vệ sinh rò rỉ", "Phòng 102", "Hôm nay", TaskItem.Priority.EMERGENCY, TaskItem.TaskStatus.TODO, "Đội kỹ thuật", "Kiểm tra vòi sen và thay gioăng nếu cần."),
                new TaskItem("Kiểm tra bảo dưỡng điều hòa hỏng", "Phòng 305", "Hôm nay", TaskItem.Priority.HIGH, TaskItem.TaskStatus.IN_PROGRESS, "Nguyễn Văn Kỹ thuật", "Kiểm tra nguồn điện, gas và dàn lạnh."),
                new TaskItem("Bàn giao chìa khóa & kiểm tra phòng", "Phòng trống 108", "Ngày mai", TaskItem.Priority.MEDIUM, TaskItem.TaskStatus.TODO, "Lễ tân", "Kiểm tra tài sản và lập biên bản bàn giao."),
                new TaskItem("Thay bóng đèn hành lang tầng 2", "Khu vực chung", "26/08", TaskItem.Priority.MEDIUM, TaskItem.TaskStatus.IN_PROGRESS, "Đội kỹ thuật", "Thay bóng LED và kiểm tra công tắc."),
                new TaskItem("Chốt số công tơ điện nước cuối tháng", "Toàn bộ 48 phòng", "30/08", TaskItem.Priority.HIGH, TaskItem.TaskStatus.TODO, "Ban quản lý", "Ghi nhận chỉ số và đối soát hóa đơn."),
                new TaskItem("Xịt khử khuẩn & dọn hành lang", "Tầng 1 - 3", "27/08", TaskItem.Priority.MEDIUM, TaskItem.TaskStatus.DONE, "Vệ sinh", "Đã hoàn thành theo lịch định kỳ.")
        ));

        // Revenue 6 months
        revenueData = Arrays.asList(
                new RevenueData("T3/24", 142.0),
                new RevenueData("T4/24", 149.0),
                new RevenueData("T5/24", 151.0),
                new RevenueData("T6/24", 153.0),
                new RevenueData("T7/24", 156.0),
                new RevenueData("T8/24", 157.0)
        );

        // Messages
        messages = new ArrayList<>(Arrays.asList(
                new MessageItem("Nguyễn Văn An", "P.101", "Chào anh Tuấn, em đã chuyển khoản tiền phòng tháng 8 rồi nhé ạ!", "09:15", false, false),
                new MessageItem("Chủ nhà trọ", "P.101", "Anh nhận được rồi nhé An, cảm ơn em!", "09:20", true, false),
                new MessageItem("Trần Thị Lan", "P.102", "Anh ơi vòi sen phòng tắm bị rỉ nước, anh cho thợ kiểm tra giúp em với ạ.", "10:05", false, true),
                new MessageItem("Chủ nhà trọ", "P.102", "Ok em, chiều nay khoảng 2h thợ qua sửa nhé!", "10:12", true, false),
                new MessageItem("Phạm Thị Thùy", "P.202", "Anh Tuấn ơi, tháng 11 hết hạn HĐ em muốn gia hạn thêm 1 năm nữa ạ.", "11:30", false, false)
        ));

                syncContractPartyDetails();
    }

        private void syncContractPartyDetails() {
                for (Contract contract : contracts) {
                        tenants.stream()
                                        .filter(tenant -> tenant.getName().equalsIgnoreCase(contract.getTenantName())
                                                        || tenant.getRoomName().equalsIgnoreCase(contract.getRoomName()))
                                        .findFirst()
                                        .ifPresent(tenant -> {
                                                if (contract.getPhone() == null || contract.getPhone().isBlank()) contract.setPhone(tenant.getPhone());
                                                contract.setTenantIdCard(tenant.getIdCard());
                                                contract.setTenantAddress(tenant.getAddress().isBlank() ? tenant.getHometown() : tenant.getAddress());
                                        });
                        contract.setOwnerName("Khu trọ Hòa Bình");
                        contract.setOwnerAddress("Số 18, Ngõ 45, Đường Hòa Bình, Cầu Giấy, Hà Nội");
                        contract.setOwnerPhone("0988 123 456");
                }
        }

    public List<Room> getRooms() { return rooms; }
    public List<Tenant> getTenants() { return tenants; }
    public List<Contract> getContracts() { return contracts; }
    public List<Invoice> getInvoices() { return invoices; }
    public List<Asset> getAssets() { return assets; }
    public List<TaskItem> getTasks() { return tasks; }
    public List<RevenueData> getRevenueData() { return revenueData; }
    public List<MessageItem> getMessages() { return messages; }

        public Room findRoom(String roomName) {
                return rooms.stream().filter(room -> room.getName().equals(roomName)).findFirst().orElse(null);
        }

        public Invoice findInvoice(String roomName, String monthPeriod) {
                return invoices.stream().filter(invoice -> invoice.getRoomName().equals(roomName)
                                && invoice.getMonthPeriod().equalsIgnoreCase(monthPeriod)).findFirst().orElse(null);
        }

        public long getPreviousElectricReading(String roomName) {
                return invoices.stream().filter(invoice -> invoice.getRoomName().equals(roomName))
                                .max(Comparator.comparing(Invoice::getMonthPeriod))
                                .map(Invoice::getNewElectricReading).orElse(0L);
        }

        public long getPreviousWaterReading(String roomName) {
                return invoices.stream().filter(invoice -> invoice.getRoomName().equals(roomName))
                                .max(Comparator.comparing(Invoice::getMonthPeriod))
                                .map(Invoice::getNewWaterReading).orElse(0L);
        }

        public long getPreviousDebt(String roomName) {
                return invoices.stream().filter(invoice -> invoice.getRoomName().equals(roomName)
                                                && invoice.getStatus() != Invoice.InvoiceStatus.PAID)
                                .mapToLong(invoice -> Math.max(0, invoice.getTotalAmount() - invoice.getPaidAmount())).sum();
        }

        public int getTotalRooms() { return rooms.size(); }
        public int getRentedRooms() {
                return (int) rooms.stream().filter(room -> room.getStatus() == RoomStatus.RENTED).count();
        }
        public int getVacantRooms() {
                return (int) rooms.stream().filter(room -> room.getStatus() == RoomStatus.VACANT).count();
        }
        public String getOccupancyRateFormatted() {
                return String.format("%.1f%%", getOccupancyRate() * 100);
        }
        public double getOccupancyRate() {
                return rooms.isEmpty() ? 0 : (double) getRentedRooms() / rooms.size();
        }
        public String getMonthlyRevenue() {
                return formatMoney(invoices.stream().filter(invoice -> invoice.getStatus() == Invoice.InvoiceStatus.PAID)
                                .mapToLong(Invoice::getTotalAmount).sum());
        }
        public String getUnpaidRevenue() {
                return formatMoney(invoices.stream().filter(invoice -> invoice.getStatus() != Invoice.InvoiceStatus.PAID)
                                .mapToLong(invoice -> Math.max(0, invoice.getTotalAmount() - invoice.getPaidAmount())).sum());
        }

        private String formatMoney(long amount) {
                return String.format("%,dđ", amount).replace(',', '.');
        }
}