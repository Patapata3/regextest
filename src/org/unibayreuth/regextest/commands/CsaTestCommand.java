package org.unibayreuth.regextest.commands;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.unibayreuth.regextest.automata.deterministic.CSAutomaton;
import org.unibayreuth.regextest.automata.deterministic.DFAutomaton;
import org.unibayreuth.regextest.compilers.NCFARegexCompiler;
import org.unibayreuth.regextest.compilers.NFARegexCompiler;
import org.unibayreuth.regextest.compilers.utils.CompileUtils;
import org.unibayreuth.regextest.compilers.utils.ncfa.RegexTree;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

public class CsaTestCommand implements Command<String>{
    public static final String NAME = "csatest";
    @Override
    public String execute(String[] args) {
        String regex;
        int maxLength, times;
        try {
            regex = args[1];
            maxLength = Integer.parseInt(args[2]);
            times = Integer.parseInt(args[3]);
        } catch (Exception e) {
            return String.format("Invalid arguments: %s", e.getMessage());
        }

        List<Character> alphabet = new ArrayList<>(CompileUtils.parseRegexTree(regex).getAlphabet());
        DFAutomaton referenceAutomaton = new NFARegexCompiler().compile(regex).determine();
        CSAutomaton testedAutomaton = new NCFARegexCompiler().compile(regex).determine();

        List<Double> results = new ArrayList<>();
        for (int i = 1; i <= maxLength; i++) {
            int errorCount = 0;
            for (int j = 0; j < times; j++) {
                Random random = new Random();
                String input = random.ints(0, alphabet.size())
                        .limit(i)
                        .mapToObj(alphabet::get)
                        .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                        .toString();

                boolean reference = referenceAutomaton.match(input);
                boolean tested = testedAutomaton.match(input);
                if (reference != tested) {
                    errorCount++;
                }
            }
            double errorRatio = ((double)errorCount) / times * 100;
            results.add(errorRatio);
            System.out.printf("length=%d; error ratio=%.2f%%%n", i, ((double)errorCount) / times * 100);
        }
        try {
            exportResults(results);
        } catch (IOException e) {
            return String.format("Issues with saving results to a file: %s", e.getMessage());
        }
        return "Test finished";
    }

    private void exportResults(List<Double> results) throws IOException {
        try (XSSFWorkbook workbook = new XSSFWorkbook();
             FileOutputStream outputStream = new FileOutputStream(String.format("csaTest_%s.xlsx", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy'T'HH-mm-ss"))))) {
            Sheet sheet = workbook.createSheet();
            initHeaders(sheet);
            fillData(sheet, results);
            workbook.write(outputStream);
        }
    }

    private void initHeaders(Sheet sheet) {
        Row titleRow = sheet.createRow(0);
        titleRow.createCell(0).setCellValue("Length");
        titleRow.createCell(1).setCellValue("Error Ratio %");
    }

    private void fillData(Sheet sheet, List<Double> data) {
        for (int strLength = 1; strLength <= data.size(); strLength++) {
            Row row = sheet.createRow(strLength);
            row.createCell(0).setCellValue(strLength);
            row.createCell(1).setCellValue(data.get(strLength - 1));
        }
    }
}
