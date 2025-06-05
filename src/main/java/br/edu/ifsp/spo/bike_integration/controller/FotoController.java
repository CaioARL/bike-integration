package br.edu.ifsp.spo.bike_integration.controller;

import java.util.Base64;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.edu.ifsp.spo.bike_integration.annotation.BearerToken;
import br.edu.ifsp.spo.bike_integration.annotation.Role;
import br.edu.ifsp.spo.bike_integration.hardcode.RoleType;
import br.edu.ifsp.spo.bike_integration.service.aws.S3Service;
import br.edu.ifsp.spo.bike_integration.util.ResponseUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;

@RestController
@RequestMapping("/v1/foto")
@Tag(name = "FotoController", description = "Controller para operações relacionadas a fotos.")
public class FotoController {

    @Autowired
    private S3Service s3Service;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @Role({ RoleType.PF, RoleType.PJ })
    @BearerToken
    @GetMapping(path = "/preview", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @Operation(summary = "Faz o download de uma foto armazenada no S3 para visualização inline.")
    public ResponseEntity<byte[]> downloadFotoPreview(@RequestParam String s3Key) {
        byte[] fileBytes = s3Service.getAsByteArray(GetObjectRequest.builder()
                .bucket(bucketName)
                .key(s3Key)
                .build());
        return ResponseUtils.buildBinaryResponse(s3Key, fileBytes, true);
    }

    @Role({ RoleType.PF, RoleType.PJ })
    @BearerToken
    @GetMapping(path = "/file", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @Operation(summary = "Faz o download de uma foto armazenada no S3 como arquivo (attachment).")
    public ResponseEntity<byte[]> downloadFoto(@RequestParam String s3Key) {
        byte[] fileBytes = s3Service.getAsByteArray(GetObjectRequest.builder()
                .bucket(bucketName)
                .key(s3Key)
                .build());
        return ResponseUtils.buildBinaryResponse(s3Key, fileBytes, false);
    }

    @Role({ RoleType.PF, RoleType.PJ })
    @BearerToken
    @GetMapping(path = "/base64", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Faz o download de uma foto armazenada no S3 retornando o conteúdo em Base64.")
    public ResponseEntity<Map<String, String>> downloadFotoBytes(@RequestParam String s3Key) {
        byte[] fileBytes = s3Service.getAsByteArray(GetObjectRequest.builder()
                .bucket(bucketName)
                .key(s3Key)
                .build());
        return ResponseEntity.ok(Map.of("base64", Base64.getEncoder().encodeToString(fileBytes)));
    }
}