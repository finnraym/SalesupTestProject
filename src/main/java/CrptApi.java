import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class CrptApi {
    private final static String CRPT_CREATE_URI = "https://ismp.crpt.ru/api/v3/lk/documents/create";
    private final long delayTime;
    private final int requestLimit;
    public CrptApi(TimeUnit timeUnit, int requestLimit) {
        this.delayTime = timeUnit.toMillis(1);
        this.requestLimit = requestLimit;
    }

    public void createDocument(Document document, String signature) {
        Semaphore semaphore = new Semaphore(requestLimit);
        try {
            semaphore.acquire();
            doRequest(document, signature);
            Thread.sleep(delayTime);
            semaphore.release();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void doRequest(Document document, String signature) {
        ObjectMapper objectMapper = new ObjectMapper();
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(CRPT_CREATE_URI))
                .header("signature", signature)
                .header("content-type", "application/json")
                .POST(HttpRequest.BodyPublishers
                        .ofString(objectMapper.valueToTree(document).toString()))
                .build();
        try {
            HttpClient.newHttpClient()
                    .send(httpRequest, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}

@Data
class Document {

    private Description description;
    @JsonProperty("doc_id")
    private String docId;
    @JsonProperty("doc_status")
    private String docStatus;
    @JsonProperty("doc_type")
    private String docType;
    private boolean importRequest;
    @JsonProperty("owner_inn")
    private String ownerInn;
    @JsonProperty("participant_inn")
    private String participantInn;
    @JsonProperty("producer_inn")
    private String producerInn;
    @JsonProperty("production_date")
    private String productionDate;
    @JsonProperty("production_type")
    private String productionType;
    private List<Product> products;
    @JsonProperty("reg_date")
    private String regDate;
    @JsonProperty("reg_number")
    private String regNumber;
}

@Data
class Product {
    @JsonProperty("certificate_document")
    private String certificateDocument;
    @JsonProperty("certificate_document_date")
    private String certificateDocumentDate;
    @JsonProperty("certificate_document_nate")
    private String certificateDocumentNumber;
    @JsonProperty("owner_inn")
    private String ownerInn;
    @JsonProperty("producer_inn")
    private String producerInn;
    @JsonProperty("production_date")
    private String productionDate;
    @JsonProperty("tnved_code")
    private String tnvedCode;
    @JsonProperty("uit_code")
    private String uitCode;
    @JsonProperty("uitu_code")
    private String uituCode;

}

@Data
class Description {
    private String participantInn;
}
