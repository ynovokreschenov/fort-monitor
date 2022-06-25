import groovyx.net.http.HTTPBuilder
import static groovyx.net.http.ContentType.*
import groovy.json.JsonBuilder
import org.apache.http.client.config.RequestConfig
import org.apache.http.impl.client.HttpClients

// авторизация
// https://glonassagro.com/api/help/index#!/ExternalApiV1/ExternalApiV1_Connect
def connect(){
    // ПАРАМЕТРЫ СЕРВЕРА
    def rootOb = api.utils.get('root', [:])
    def SERVER = rootOb.fmServer
    def API = '/api/integration/v1/'
    def LOGIN = rootOb.fmLogin
    def PASSWORD = rootOb.fmPassword    
    def METHOD = 'connect'
  
    // Параметры HTTP клиента
    def TIMEOUT = 10000
    def requestConfig = RequestConfig.custom()
    .setConnectionRequestTimeout(TIMEOUT)
    .setConnectTimeout(TIMEOUT)
    .setSocketTimeout(TIMEOUT)
    .build()
    
    def http = new HTTPBuilder(SERVER)
    http.client = HttpClients.custom().setDefaultRequestConfig(requestConfig).build()
    
    def query = [
        'login' : LOGIN,
        'password' : PASSWORD,
        'lang' : 'ru-ru',
        'timezone' : '0'
        ]
    def serverResponse = ['result':null, 'error':null]

    try {
        http.get(path: API + METHOD, query: query) { response, json ->
            if (response.statusLine.statusCode in [200, 201, 204]) {
              if (json.contains('Ok')){
                serverResponse['result'] = response.headers['SessionId'].toString()-'SessionId: '
              }
            }  
        }
    } catch (Exception e) {
        logger.error("Возникла ошибка при попатке обмена данными с ${SERVER}", e)
        serverResponse['error'] = e.message
    }
    finally {
        http.shutdown()
    }
    return serverResponse
}

// завершение сессии
// https://glonassagro.com/api/help/index#!/ExternalApiV1/ExternalApiV1_Disconnect
def disconnect(def sid){
    // ПАРАМЕТРЫ СЕРВЕРА
    def rootOb = api.utils.get('root', [:])
    def SERVER = rootOb.fmServer
    def API = '/api/integration/v1/'
    def LOGIN = rootOb.fmLogin
    def PASSWORD = rootOb.fmPassword    
    def METHOD = 'disconnect'

    // Параметры HTTP клиента
    def TIMEOUT = 10000
    def requestConfig = RequestConfig.custom()
    .setConnectionRequestTimeout(TIMEOUT)
    .setConnectTimeout(TIMEOUT)
    .setSocketTimeout(TIMEOUT)
    .build()
    
    def http = new HTTPBuilder(SERVER)
    http.client = HttpClients.custom().setDefaultRequestConfig(requestConfig).build()
    http.setHeaders(['SessionId':sid])
    
    def query = [:]
    def serverResponse = ['result':null, 'error':null]

    try {
        http.get(path: API + METHOD, query: query) { response, json ->
            if (response.statusLine.statusCode in [200, 201, 204]) {
              //if (json.result == 'Ok'){
                serverResponse['result'] = json
              //}
            }  
        }
    } catch (Exception e) {
        logger.error("Возникла ошибка при попатке обмена данными с ${SERVER}", e)
        serverResponse['error'] = e.message
    }
    finally {
        http.shutdown()
    }
    return serverResponse
}

def httpRequest(def method = 'get', def path = '', def query = [:], def headers = [:]){
    // ПАРАМЕТРЫ СЕРВЕРА
    def rootOb = api.utils.get('root', [:])
    def SERVER = rootOb.fmServer
    def API = '/api/integration/v1/'
  
    // Определим переменную для хранания результата ответа от сервера
    def serverResponse = ['result':null, 'error':null]

    //Формируем параметры в формате json
    //def parameters = new JsonBuilder(body)

    //Создайте HTTP клиента
    def TIMEOUT = 10000
    def requestConfig = RequestConfig.custom()
    .setConnectionRequestTimeout(TIMEOUT)
    .setConnectTimeout(TIMEOUT)
    .setSocketTimeout(TIMEOUT)
    .build()
  
    def http = new HTTPBuilder(SERVER)
    http.client = HttpClients.custom().setDefaultRequestConfig(requestConfig).build()
    
    //http.setHeaders([Content-Type: "application/x-www-form-urlencoded"]) 
    //if (headers){
    http.setHeaders(headers)
    //}
    // Опционально включите игнорирование ошибок SSL
    //http.ignoreSSLIssues()
    
    // Выполняем запрос к внешней системе и обрабатываем ответ
    try {
        http.get(path: API+path, query: query) { response, json ->
            if (response.statusLine.statusCode in [200, 201, 204]) {
                serverResponse['result'] = json//.toString()
            }  
        }
    } catch (Exception e) {
        logger.error("Возникла ошибка при попытке обмена данными с ${SERVER}", e)
        serverResponse['error'] = e.message
    }
    finally {
        http.shutdown()
    }
    return serverResponse
}
