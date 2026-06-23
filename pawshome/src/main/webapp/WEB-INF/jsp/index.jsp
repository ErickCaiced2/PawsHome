<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8"/>
    <title>PawsHome</title>
    <style>
        body { font-family: Arial, sans-serif; max-width: 600px; margin: 60px auto; }
        .status { padding: 16px; border-radius: 8px; margin-top: 20px; }
        .ok  { background: #d4edda; color: #155724; border: 1px solid #c3e6cb; }
        .err { background: #f8d7da; color: #721c24; border: 1px solid #f5c6cb; }
        .dot { display: inline-block; width: 12px; height: 12px; border-radius: 50%; margin-right: 8px; }
        .dot-ok  { background: #28a745; }
        .dot-err { background: #dc3545; }
    </style>
</head>
<body>
    <h1>PawsHome</h1>

    <div class="${connected ? 'status ok' : 'status err'}">
        <span class="${connected ? 'dot dot-ok' : 'dot dot-err'}"></span>
        <strong>${connected ? 'Base de datos conectada' : 'Sin conexión a la base de datos'}</strong>
        <p>${dbStatus}</p>
    </div>
</body>
</html>
