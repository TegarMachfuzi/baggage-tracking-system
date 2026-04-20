package com.baggage.notification.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.oned.Code128Writer;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;

@Service
public class BarcodeGeneratorService {

    public byte[] generateBarcode(String content) throws Exception {
        Code128Writer writer = new Code128Writer();
        BitMatrix matrix = writer.encode(content, BarcodeFormat.CODE_128, 400, 100);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(matrix, "PNG", out);
        return out.toByteArray();
    }
}
