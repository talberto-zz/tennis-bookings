web: target/universal/stage/bin/tennis-reservations \
	-Dhttp.port=${PORT} \
	-DapplyEvolutions.default=true \
	-Ddb.default.driver=org.postgresql.Driver \
	-Ddb.default.url="jdbc:${DB_TYPE}://${DB_HOST}:${DB_PORT}/${DB_NAME}?user=${DB_USER}&password=${DB_PASS}" \
	-DwebDriver.url="${WEBDRIVER_URL}" \
	-DwebDriver.chrome="${WEBDRIVER_BROWSER}" \
	-Dhttp.auth.username="tennis"
	-Dhttp.auth.password="guapayguapo2013"
