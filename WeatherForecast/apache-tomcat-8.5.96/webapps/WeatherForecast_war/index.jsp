<%@ page import="java.util.List" %>
<%@ page import="Entity.DataItem" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Weather Forecast</title>
    <!-- Thêm các thư viện và tài nguyên cần thiết cho DataTable -->
    <link rel="stylesheet" type="text/css" href="https://cdn.datatables.net/1.10.24/css/jquery.dataTables.css">
    <style>
        /* Thiết lập màu chủ đạo và font chữ cho toàn bộ trang */
        body {
            font-family: Arial, sans-serif;
            background-color: #f0f8ff; /* Màu nền cho body */
            color: #333; /* Màu chữ chính */
            margin: 0; /* Loại bỏ margin mặc định */
            padding: 0; /* Loại bỏ padding mặc định */
        }

        /* Header */
        header {
            background-color: #008000; /* Màu nền cho header */
            color: #fff; /* Màu chữ cho header */
            padding: 20px;
            text-align: center;
        }

        /* Navigation menu */
        nav {
            background-color: #e0f2f1; /* Màu nền cho thanh menu */
            padding: 10px;
            text-align: center;
        }

        /* Main content */
        main {
            padding: 20px;
        }

        /* Table styles */
        table {
            border-collapse: collapse;
            width: 100%;
        }

        th, td {
            border: 1px solid #ccc;
            padding: 8px;
        }

        th {
            background-color: #aaabd0;
            color: black;
            text-align: left;
        }

        tbody tr:nth-child(even) {
            background-color: #e5e5f6;
        }

        tbody tr:hover {
            background-color: #c7c6c6;
        }

        img {
            max-width: 50px;
            max-height: 50px;
        }

        /* Footer */
        footer {
            background-color: #333;
            color: #fff;
            text-align: center;
            padding: 10px;
            position: fixed;
            bottom: 0;
            width: 100%;
        }
        #selectedCity {
            padding: 8px;
            font-size: 16px;
            border: 1px solid #ccc;
            border-radius: 4px;
            outline: none;
        }
    </style>
    <script type="text/javascript" charset="utf8" src="https://code.jquery.com/jquery-3.5.1.js"></script>
    <script type="text/javascript" charset="utf8" src="https://cdn.datatables.net/1.10.24/js/jquery.dataTables.js"></script>
</head>
<body>
<form action="HomePage" method="get" id="cityForm">
<table id="dataTable">
    <% List<String> listCity = (List<String>) request.getAttribute("listCity");
        String selectedCity = request.getParameter("selectedCity"); %>

        <h1>Weather forecast area:
            <select name="selectedCity" id="selectedCity">
                <option value="">Choose a city</option>
                <% for (String city : listCity) { %>
                <option value="<%= city %>" <% if (city.equals(selectedCity)) { %> selected <% } %>><%= city %></option>
                <% } %>
            </select>
        </h1>

    <thead>
    <tr>
        <th>STT</th>
        <th>Day Of Week</th>
        <th>Time</th>
        <th>Date</th>
        <th>Temp</th>
        <th>Pressure</th>
        <th>Humidity</th>
        <th>Clouds</th>
        <th>WindSpeed</th>
        <th>Visibility</th>
        <th>Rain of 3h</th>
        <th>Weather Description</th>
        <th>Weather Icon</th>
    </tr>
    </thead>
    <tbody>
    <% int count = 1;%>
    <% List<DataItem> dataList = (List<DataItem>) request.getAttribute("dataList");
        for (DataItem item : dataList) {
    %>
    <tr>
        <td><%=  count++ %></td>
        <td><%= item.getDateOfWeek()%></td>
        <td><%= item.getTimeForecast()%></td>
        <td><%= item.getDateForecast()%></td>
        <td><%=  item.getMainTemp() %>°C</td>
        <td><%=  item.getMainPressure() %>hPa</td>
        <td><%=  item.getMainHumidity() %>%</td>
        <td><%=  item.getCloudsAll() %>%</td>
        <td><%=  item.getWindSpeed() %>miles/hour</td>
        <td><%=  item.getVisibility() %>m</td>
        <td><%=  item.getRain3h() %>mm</td>
        <td><%=  item.getWeatherDescription() %></td>
        <td><img src="image/w<%= item.getWeatherIcon()%>.png" /></td>
    </tr>
    <% } %>
    </tbody>
</table>
</form>
<script>
    document.getElementById('selectedCity').addEventListener('change', function() {
        var selectedCity = this.value;
        var form = document.getElementById('cityForm');
        form.action = "HomePage?selectedCity=" + selectedCity;
        form.method = "get";
        form.submit(); // Gửi yêu cầu khi có sự thay đổi giá trị
    });
</script>
<script>
    $(document).ready(function() {
        $('#dataTable').DataTable(); // Kích hoạt DataTable trên bảng có id là 'dataTable'
    });
</script>
</body>
