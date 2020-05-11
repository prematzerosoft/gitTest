package com.UBS.FOF.service;

import static java.util.stream.Collectors.toMap;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.UBS.FOF.util.UploadUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;



@Service
public class UploadService {

    private final UploadUtil uploadUtil;



    public UploadService(UploadUtil uploadUtil) {
        this.uploadUtil = uploadUtil;
    }

    public List<Map<String, String>> upload(MultipartFile file) throws Exception {

        Path tempDir = Files.createTempDirectory("");

        File tempFile = tempDir.resolve(file.getOriginalFilename()).toFile();

        file.transferTo(tempFile);

        Workbook workbook = WorkbookFactory.create(tempFile);

        Sheet sheet = workbook.getSheetAt(0);



        Supplier<Stream<Row>> rowStreamSupplier = uploadUtil.getRowStreamSupplier(sheet);



        Row headerRow = rowStreamSupplier.get().findFirst().get();




        List<String> headerCells = uploadUtil.getStream(headerRow)
                .map(Cell::getStringCellValue)
                .map(String::valueOf)
                .collect(Collectors.toList());

        int colCount = headerCells.size();


        return rowStreamSupplier.get()
                .skip(1)
                .map(row -> {

                    List<String> cellList = uploadUtil.getStream(row)
                            .map(Cell::getStringCellValue)
                            .collect(Collectors.toList());

                    return uploadUtil.cellIteratorSupplier(colCount)
                            .get()
                            .collect(toMap(headerCells::get, cellList::get));
                })
                .collect(Collectors.toList());


    }


    public List<Map<String, String>> fetch() throws Exception {


        FileInputStream fis=new FileInputStream(new File("./src/fof.xlsx"));


        XSSFWorkbook wb=new XSSFWorkbook(fis);
        Sheet sheet = wb.getSheetAt(0);



        Supplier<Stream<Row>> rowStreamSupplier = uploadUtil.getRowStreamSupplier(sheet);



        Row headerRow = rowStreamSupplier.get().findFirst().get();




        List<String> headerCells = uploadUtil.getStream(headerRow)
                .map(Cell::getStringCellValue)
                .map(String::valueOf)
                .collect(Collectors.toList());

        int colCount = headerCells.size();


        return rowStreamSupplier.get()
                .skip(1)
                .map(row -> {

                    List<String> cellList = uploadUtil.getStream(row)
                            .map(Cell::getStringCellValue)
                            .collect(Collectors.toList());

                    return uploadUtil.cellIteratorSupplier(colCount)
                            .get()
                            .collect(toMap(headerCells::get, cellList::get));
                })
                .collect(Collectors.toList());


    }


}