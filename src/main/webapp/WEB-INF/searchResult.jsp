<!DOCTYPE HTML>
<html xmlns:th="http://www.thymeleaf.org">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
</head>
<body>
	<ol>
		<li th:each="docItem : ${searchResults}"><b><span
				th:text="${document.title}"></span></b> - <span
			th:text="${document.relevanceScore}"></span> -</li>
	</ol>
</body>
</html>