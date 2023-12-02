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
        <th>STT</th>
        <th>CityName</th>
        <th>DateTime</th>
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
        <td><%=  item.getCityName() %></td>
        <td><%=   item.getDateOfWeek() + " "+ item.getTimeForecast() + " "+ item.getDateForecast().toString() %></td>
        <td><%=  item.getMainTemp() +"°C" %></td>
        <td><%=  item.getMainPressure() %></td>
        <td><%=  item.getMainHumidity() %></td>
        <td><%=  item.getCloudsAll() %></td>
        <td><%=  item.getWindSpeed() %></td>
        <td><%=  item.getVisibility() %></td>
        <td><%=  item.getRain3h() %></td>
        <td><%=  item.getWeatherDescription() %></td>
        <td><%=  item.getWeatherIcon() %></td>
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
