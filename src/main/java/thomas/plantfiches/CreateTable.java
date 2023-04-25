package thomas.plantfiches;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class CreateTable {

    public static final String IMAGE_LOCATION = "/Volumes/GoogleDrive/My Drive/Personal Documents/tuinontwerp/vermaelen-vandenberghe/fotos/ed";

    public static void main(String[] args) throws Exception {
        String plantRecord = "/Users/thomas/Desktop/Plant Record.txt";
        List<Path> files = Files.walk(Paths.get(IMAGE_LOCATION)).filter(p -> !Files.isDirectory(p) && isImage(p)).sorted().collect(Collectors.toList());
        List<PlantRecord> plants = new ArrayList<>();
        LinkedHashMap<PlantRecord, Path> plantsM = new LinkedHashMap<>();
        try (XWPFDocument doc = new XWPFDocument()) {
            try (BufferedReader br = new BufferedReader(new FileReader(plantRecord))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] split = line.split(",");
                    PlantRecord record = new PlantRecord(split[0], Integer.parseInt(split[1]), split[2], split[3]);
                    record.addToList(plants);
                }
            }
            Collections.sort(plants);
            for (PlantRecord p : plants) {
                plantsM.put(p, null);
                for (Path file : files) {
                    if (p.fileNameFits(file)) {
                        plantsM.put(p, file);
                    }
                }
            }

            XWPFTable table = getImagesTable(doc, plantsM);
            // save to .docx file
            System.out.println("Saving file");
            try (FileOutputStream out = new FileOutputStream("/Users/thomas/Desktop/file.docx")) {
                doc.write(out);
            }
        }
    }


    private static boolean isImage(Path file) {
        String fileName = file.getFileName().toString().toLowerCase();
        return fileName.endsWith(".jpeg") || fileName.endsWith(".jpg") || fileName.endsWith(".png");
    }

    private static XWPFTable getImagesTable(XWPFDocument doc, LinkedHashMap<PlantRecord, Path> files) throws
            IOException, InvalidFormatException {
        XWPFTable table = doc.createTable();
        List<Map.Entry<PlantRecord, Path>> entries = new ArrayList(files.entrySet());
        for (int i = 0; i < files.size(); i = i + 2) {
            // Map.Entry entry = entries.get(i);
            Path file1 = entries.get(i).getValue();
            PlantRecord record1 = entries.get(i).getKey();

            XWPFTableRow row = table.createRow();
            XWPFTableCell cell = row.getCell(0);
            addCell(file1, record1.subscript(), row, cell);
            if (i < files.size() - 1) {
                Path file2 = entries.get(i+1).getValue();
                PlantRecord record2 = entries.get(i+1).getKey();
                cell = row.addNewTableCell();
                addCell(file2, record2.subscript(), row, cell);
            }
        }
        return table;
    }

    public static void addCell(Path file, String subscript, XWPFTableRow row, XWPFTableCell cell) throws
            IOException, InvalidFormatException {
        if (file != null) {
            addImage(cell.addParagraph(), file);
        }
        XWPFParagraph tp = cell.addParagraph();
        XWPFRun run = tp.createRun();
        run.setText(subscript);
        tp.setAlignment(ParagraphAlignment.CENTER);
    }

    public static void addImage(XWPFParagraph p, Path file) throws IOException, InvalidFormatException {
        p.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun r = p.createRun();
        r.addBreak();

        try (FileInputStream is = new FileInputStream(file.toString())) {
            r.addPicture(is, Document.PICTURE_TYPE_PNG,    // png file
                    file.toString(), Units.toEMU(170), Units.toEMU(170));
        }
    }
}
