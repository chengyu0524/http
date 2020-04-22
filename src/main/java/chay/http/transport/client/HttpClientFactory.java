package chay.http.transport.client;


public final class HttpClientFactory {
  private static HttpClientFactory instance = new HttpClientFactory();

  private HttpClient httpClient;

  public static HttpClientFactory getInstance() {
    return instance;
  }

  private HttpClientFactory() {

  }

  public HttpClient getHttpClient() {
    return httpClient;
  }

  public void setHttpClient(HttpClient httpClient) {
    this.httpClient = httpClient;
  }

}
