<%@ page import="java.util.List" %>
<%@ page import="Entity.DataItem" %>
<html>
<head>
    <!-- Thêm các thư viện và tài nguyên cần thiết cho DataTable -->
    <link rel="stylesheet" type="text/css" href="https://cdn.datatables.net/1.10.24/css/jquery.dataTables.css">
    <script type="text/javascript" charset="utf8" src="https://code.jquery.com/jquery-3.5.1.js"></script>
    <script type="text/javascript" charset="utf8" src="https://cdn.datatables.net/1.10.24/js/jquery.dataTables.js"></script>
</head>
<body>
<table id="dataTable">
    <thead>
    <tr>
        <th>ID</th>
        <th>Name</th>
        <!-- Thêm các cột khác tùy theo cấu trúc của bảng -->
    </tr>
    </thead>
    <tbody>
    <% List<DataItem> dataList = (List<DataItem>) request.getAttribute("dataList");
        for (DataItem item : dataList) {
    %>
    <tr>
        <td><%= item.getId() %></td>
        <td><%=  item.getCityName() %></td>
        <td><%=  item.getDateOfWeek() %></td>
        <td><%=  item.getDateForecast().toString() %></td>
        <td><%=  item.getCityName() %></td>
    </tr>
    <%
        }
    %>
    </tbody>
</table>

<script>
    // Kích hoạt DataTable trên bảng có ID là "dataTable"
    $(document).ready(function() {
        $('#dataTable').DataTable();
    });
</script>
</body>
</html>
