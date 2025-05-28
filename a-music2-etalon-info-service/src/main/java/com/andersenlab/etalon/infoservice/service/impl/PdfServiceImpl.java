package com.andersenlab.etalon.infoservice.service.impl;

import static com.andersenlab.etalon.infoservice.util.Constants.IMAGE_PATH;

import com.andersenlab.etalon.infoservice.dto.common.TransferReceiptContext;
import com.andersenlab.etalon.infoservice.dto.request.TransactionStatementPdfRequestDto;
import com.andersenlab.etalon.infoservice.dto.response.TransactionDetailedResponseDto;
import com.andersenlab.etalon.infoservice.dto.response.UserDataResponseDto;
import com.andersenlab.etalon.infoservice.exception.TechnicalException;
import com.andersenlab.etalon.infoservice.service.PdfService;
import com.andersenlab.etalon.infoservice.util.enums.Type;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import javax.imageio.ImageIO;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.context.MessageSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PdfServiceImpl implements PdfService {
  private static final float POINTS_PER_INCH = 72;

  private static final int[] ROWS_VERTICAL_OFFSET =
      new int[] {158, 65, 78, 97, 97, 87, 87, 113, 98, 144};
  private static final String NUMBER_SPLIT_REGEX = "(.{4})(?=\\d)";

  private final MessageSource messageSource;

  @Override
  public File createTransactionPdf(TransactionStatementPdfRequestDto dto, Locale locale) {
    try (PDDocument document = new PDDocument()) {
      addTransactionContentToPage(document, dto, locale);
      return createTempFile(document, "transaction-");
    } catch (IOException e) {
      throw new TechnicalException(
          HttpStatus.INTERNAL_SERVER_ERROR, TechnicalException.ERROR_CREATING_PDF);
    }
  }

  @Override
  public File createTransactionsStatement(
      UserDataResponseDto userData,
      List<TransactionDetailedResponseDto> transactionDetailedDtoList,
      String period,
      String locale) {

    try (PDDocument document = new PDDocument()) {
      addTransactionsHistoryContentToPage(
          document, transactionDetailedDtoList, userData, period, Locale.forLanguageTag(locale));
      return createTempFile(document, "transactions-history");
    } catch (IOException e) {
      throw new TechnicalException(
          HttpStatus.INTERNAL_SERVER_ERROR, TechnicalException.ERROR_CREATING_PDF);
    }
  }

  private void addTransactionContentToPage(
      PDDocument document, TransactionStatementPdfRequestDto dto, Locale locale)
      throws IOException {
    ClassPathResource defaultFontResource = new ClassPathResource("fonts/Roboto-Regular.ttf");
    PDFont defaultFont;
    try (InputStream fontStream = defaultFontResource.getInputStream()) {
      defaultFont = PDType0Font.load(document, fontStream);
    }
    PDPage page = new PDPage(PDRectangle.A4);
    document.addPage(page);
    try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
      addImageToPage(document, page, contentStream);
      addTextToPage(defaultFont, contentStream, dto, locale);
    }
  }

  private void addTransactionsHistoryContentToPage(
      PDDocument document,
      List<TransactionDetailedResponseDto> transactionDetailedDtoList,
      UserDataResponseDto userData,
      String period,
      Locale locale)
      throws IOException {
    PDFont defaultFont;
    PDFont boldFont;

    try (InputStream defaultFontStream =
            new ClassPathResource("fonts/Roboto-Regular.ttf").getInputStream();
        InputStream boldFontStream =
            new ClassPathResource("fonts/Roboto-Bold.ttf").getInputStream()) {
      defaultFont = PDType0Font.load(document, defaultFontStream);
      boldFont = PDType0Font.load(document, boldFontStream);
    }

    PDRectangle pageSize = new PDRectangle(PDRectangle.A4.getHeight(), PDRectangle.A4.getWidth());

    float margin = 40;
    float yStart = 420;
    float yPosition = yStart;
    float tableWidth = pageSize.getWidth() - 2 * margin;
    int cols = 7;
    float rowHeight = 20;
    float colWidth = tableWidth / cols;
    float cellMargin = 5f;

    PDPageContentStream contentStream =
        startNewPageAndContentStream(defaultFont, document, pageSize, userData, period, locale);
    if (transactionDetailedDtoList.isEmpty()) {
      contentStream.beginText();
      contentStream.setFont(defaultFont, 12);
      contentStream.newLineAtOffset(40, yStart);
      contentStream.showText("You haven't made any transactions for chosen period");
      contentStream.close();
      return;
    }
    drawTableHeader(
        boldFont, contentStream, margin, yPosition, rowHeight, colWidth, cellMargin, locale);
    yPosition -= rowHeight;

    if (transactionDetailedDtoList.isEmpty()) {
      drawHorizontalLine(contentStream, margin, yPosition, tableWidth);
    } else {
      for (TransactionDetailedResponseDto transaction : transactionDetailedDtoList) {
        float dynamicRowHeight =
            calculateDynamicRowHeight(defaultFont, transaction, colWidth, rowHeight);

        if (yPosition - dynamicRowHeight < margin) {
          contentStream.close();
          contentStream =
              startNewPageAndContentStream(
                  defaultFont, document, pageSize, userData, period, locale);
          yPosition = yStart - rowHeight;
          drawTableHeader(
              boldFont, contentStream, margin, yPosition, rowHeight, colWidth, cellMargin, locale);
          yPosition -= rowHeight;
        }
        drawTableData(
            defaultFont,
            contentStream,
            margin,
            yPosition,
            tableWidth,
            rowHeight,
            colWidth,
            List.of(transaction),
            locale);
        yPosition -= dynamicRowHeight;
      }
    }
    contentStream.close();
  }

  private PDPageContentStream startNewPageAndContentStream(
      PDFont defaultFont,
      PDDocument document,
      PDRectangle pageSize,
      UserDataResponseDto userData,
      String period,
      Locale locale)
      throws IOException {
    PDPage page = new PDPage(pageSize);
    document.addPage(page);
    PDPageContentStream contentStream = new PDPageContentStream(document, page);
    addImageToPage(document, page, contentStream);
    addInfoToPage(defaultFont, contentStream, userData, period, locale);
    return contentStream;
  }

  private static float calculateDynamicRowHeight(
      PDFont defaultFont,
      TransactionDetailedResponseDto transaction,
      float colWidth,
      float rowHeight)
      throws IOException {
    float lineSpacing = rowHeight * 0.55f;
    List<String> wrappedTransactionName =
        wrapText(transaction.transactionName(), colWidth, defaultFont, 6);
    return Math.max(rowHeight, (wrappedTransactionName.size() - 1) * lineSpacing + rowHeight);
  }

  private void addImageToPage(PDDocument document, PDPage page, PDPageContentStream contentStream)
      throws IOException {
    BufferedImage bufferedImage = getBankLogo();
    PDImageXObject pdImage = LosslessFactory.createFromImage(document, bufferedImage);

    float margin = 40;
    float imageScale = 0.13f;
    float x = margin;
    float y = page.getMediaBox().getHeight() - margin - pdImage.getHeight() * imageScale - 30;

    contentStream.drawImage(
        pdImage, x, y, pdImage.getWidth() * imageScale, pdImage.getHeight() * imageScale);
  }

  private void addInfoToPage(
      PDFont defaultFont,
      PDPageContentStream contentStream,
      UserDataResponseDto userData,
      String period,
      Locale locale)
      throws IOException {
    String text1 =
        messageSource.getMessage(
            "accountholder", new Object[] {userData.firstName(), userData.lastName()}, locale);
    String text2 =
        messageSource.getMessage("statement-for-the-period", new Object[] {period}, locale);

    contentStream.setFont(defaultFont, 12);
    contentStream.beginText();
    setPositionAddText(contentStream, 40, 460, text1);
    setPositionAddText(contentStream, 0, -20, text2);
    contentStream.endText();
  }

  private void addTextToPage(
      PDFont defaultFont,
      PDPageContentStream contentStream,
      TransactionStatementPdfRequestDto dto,
      Locale locale)
      throws IOException {

    contentStream.setFont(defaultFont, 14);
    contentStream.setCharacterSpacing(0.35f);
    String[] textLines =
        new String[] {
          messageSource.getMessage(
              "fullname", new Object[] {dto.firstName(), dto.lastName()}, locale),
          messageSource.getMessage("date", new Object[] {dto.transactionDate()}, locale),
          messageSource.getMessage("time", new Object[] {dto.transactionTime()}, locale),
          messageSource.getMessage("name", new Object[] {dto.transactionName()}, locale),
          messageSource.getMessage(
              "sender",
              new Object[] {dto.outcomeAccountNumber().replaceAll(NUMBER_SPLIT_REGEX, "$1 ")},
              locale),
          messageSource.getMessage(
              "receiver",
              new Object[] {dto.incomeAccountNumber().replaceAll(NUMBER_SPLIT_REGEX, "$1 ")},
              locale),
          messageSource.getMessage(
              "amount",
              new Object[] {
                dto.transactionAmount().setScale(2, RoundingMode.HALF_UP).toString(), dto.currency()
              },
              locale),
          messageSource.getMessage(
              "type",
              new Object[] {messageSource.getMessage(dto.type().name(), new Object[] {}, locale)},
              locale),
          messageSource.getMessage(
              "status",
              new Object[] {messageSource.getMessage(dto.status().name(), new Object[] {}, locale)},
              locale)
        };
    contentStream.beginText();
    setPositionAddText(contentStream, 50, 720, "");
    for (String text : textLines) {
      setPositionAddText(contentStream, 0, -35, text);
    }
    contentStream.endText();
  }

  private void drawTableHeader(
      PDFont boldFont,
      PDPageContentStream contentStream,
      float x,
      float y,
      float rowHeight,
      float colWidth,
      float margin,
      Locale locale)
      throws IOException {
    contentStream.setLineWidth(1f);
    contentStream.setFont(boldFont, 8);

    float nextX = x;
    for (int i = 0; i < 7; i++) {
      drawVerticalLines(contentStream, nextX, y, rowHeight);
      String header =
          switch (i) {
            case 0 -> messageSource.getMessage("transaction-date-time", new Object[] {}, locale);
            case 1 -> messageSource.getMessage("sender-account", new Object[] {}, locale);
            case 2 -> messageSource.getMessage("receiver-account", new Object[] {}, locale);
            case 3 -> messageSource.getMessage("transaction-name", new Object[] {}, locale);
            case 4 -> messageSource.getMessage("transaction-status", new Object[] {}, locale);
            case 5 -> messageSource.getMessage("transaction-type", new Object[] {}, locale);
            case 6 -> messageSource.getMessage("transaction-amount", new Object[] {}, locale);
            default -> "";
          };
      contentStream.beginText();
      setPositionAddText(contentStream, nextX + margin, y - 15, header);
      contentStream.endText();
      nextX += colWidth;
    }
    drawHorizontalLine(contentStream, x, y, nextX - x);
    drawVerticalLines(contentStream, nextX, y, rowHeight);
  }

  private void drawTableData(
      PDFont defaultFont,
      PDPageContentStream contentStream,
      float x,
      float y,
      float tableWidth,
      float rowHeight,
      float colWidth,
      List<TransactionDetailedResponseDto> transactionsList,
      Locale locale)
      throws IOException {
    float cellMargin = 5f;
    float lineSpacing = rowHeight * 0.55f;
    contentStream.setLineWidth(1f);
    contentStream.setFont(defaultFont, 6);

    float nextY = y;
    for (TransactionDetailedResponseDto transaction : transactionsList) {

      float dynamicRowHeight =
          calculateDynamicRowHeight(defaultFont, transaction, colWidth, rowHeight);

      drawHorizontalLine(contentStream, x, nextY, tableWidth);
      // TODO fix currency NPE
      String currencySymbol =
          Objects.isNull(transaction.currency()) ? "" : transaction.currency().getCurrencySymbol();

      String[] rowData = {
        transaction.transactionDate() + " " + transaction.transactionTime(),
        transaction.outcomeAccountNumber(),
        transaction.incomeAccountNumber(),
        "",
        StringUtils.capitalize(
            messageSource.getMessage(transaction.status().name(), new Object[] {}, locale)),
        transaction.type().getName(),
        transaction.type().equals(Type.INCOME)
            ? String.format("+%s%s", transaction.transactionAmount().toString(), currencySymbol)
            : String.format(
                "-%s%s",
                transaction.transactionAmount().toString(),
                transaction.currency().getCurrencySymbol())
      };

      float nextX = x + cellMargin;
      float nextXLine = x;
      for (int j = 0; j < rowData.length; j++) {
        drawVerticalLines(contentStream, nextXLine, nextY, dynamicRowHeight);

        contentStream.beginText();
        if (j == 3) {
          List<String> wrappedTransactionName =
              wrapText(transaction.transactionName(), colWidth, defaultFont, 6);
          setPositionAddText(contentStream, nextX, nextY - 15, wrappedTransactionName.get(0));
          for (int i = 1; i < wrappedTransactionName.size(); i++) {
            setPositionAddText(contentStream, 0, -lineSpacing, wrappedTransactionName.get(i));
          }
        } else {
          setPositionAddText(contentStream, nextX, nextY - 15, rowData[j]);
        }
        contentStream.endText();
        nextX += colWidth;
        nextXLine += colWidth;
      }
      drawVerticalLines(contentStream, x + tableWidth, nextY, dynamicRowHeight);
      nextY -= dynamicRowHeight;
      drawHorizontalLine(contentStream, x, nextY, tableWidth);
    }
  }

  private static List<String> wrapText(String text, float colWidth, PDFont font, float fontSize)
      throws IOException {
    List<String> lines = new ArrayList<>();
    StringBuilder currentLine = new StringBuilder();
    String[] words = text.split(" ");
    for (int i = 0; i < words.length; i++) {
      String word = words[i];
      float wordWidth = getWordWidth(word, font, fontSize);
      if (wordWidth > colWidth) {
        handleLongWord(lines, currentLine, word);
      } else {
        currentLine = handleRegularWord(lines, currentLine, words, i, colWidth, font, fontSize);
      }
    }

    addRemainingLine(lines, currentLine);
    return lines;
  }

  private static float getWordWidth(String word, PDFont font, float fontSize) throws IOException {
    return font.getStringWidth(word) / 1000 * fontSize;
  }

  private static void handleLongWord(List<String> lines, StringBuilder currentLine, String word) {
    if (currentLine.length() > 0) {
      lines.add(currentLine.toString().trim());
    }
    lines.add(word);
    currentLine.setLength(0);
  }

  private static StringBuilder handleRegularWord(
      List<String> lines,
      StringBuilder currentLine,
      String[] words,
      int i,
      float colWidth,
      PDFont font,
      float fontSize)
      throws IOException {
    String word = words[i];
    float combinedWidth = font.getStringWidth(currentLine + word) / 1000 * fontSize;

    if (i < words.length - 1) {
      String nextWord = words[i + 1];
      float nextWordWidth = getWordWidth(nextWord, font, fontSize);

      if (combinedWidth + nextWordWidth > colWidth) {
        lines.add(currentLine.toString().trim());
        return new StringBuilder(word).append(StringUtils.SPACE);
      } else {
        return currentLine.append(word).append(StringUtils.SPACE);
      }
    } else {
      if (combinedWidth > colWidth) {
        lines.add(currentLine.toString().trim());
        return new StringBuilder(word).append(StringUtils.SPACE);
      } else {
        return currentLine.append(word).append(StringUtils.SPACE);
      }
    }
  }

  private static void addRemainingLine(List<String> lines, StringBuilder currentLine) {
    if (currentLine.length() > 0) {
      lines.add(currentLine.toString().trim());
    }
  }

  private static void drawHorizontalLine(
      PDPageContentStream contentStream, float x, float y, float tableWidth) throws IOException {
    contentStream.moveTo(x, y);
    contentStream.lineTo(x + tableWidth, y);
    contentStream.stroke();
  }

  private static void drawVerticalLines(
      PDPageContentStream contentStream, float x, float y, float rowHeight) throws IOException {
    contentStream.moveTo(x, y);
    contentStream.lineTo(x, y - rowHeight);
    contentStream.stroke();
  }

  private static void setPositionAddText(
      PDPageContentStream contentStream, float x, float y, String text) throws IOException {
    contentStream.newLineAtOffset(x, y);
    contentStream.showText(text);
  }

  private BufferedImage getBankLogo() {
    BufferedImage bufferedImage;
    try {
      bufferedImage = ImageIO.read(Objects.requireNonNull(getClass().getResource(IMAGE_PATH)));
    } catch (IOException e) {
      throw new TechnicalException(
          HttpStatus.INTERNAL_SERVER_ERROR, TechnicalException.ERROR_CREATING_IMAGE_FILE);
    }
    return bufferedImage;
  }

  private File createTempFile(PDDocument document, String prefix) throws IOException {
    File tempDir = new File(System.getProperty("java.io.tmpdir"));
    File pdfFile = File.createTempFile(prefix, ".pdf", tempDir);
    document.save(pdfFile);
    return pdfFile;
  }

  @Override
  public File createTransactionReceipt(TransferReceiptContext receipt) {
    try (PDDocument document = new PDDocument()) {
      PDPage page = new PDPage(new PDRectangle(5 * POINTS_PER_INCH, 6 * POINTS_PER_INCH));
      document.addPage(page);
      try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
        List<Block> content = createReceiptPageContent(document, receipt);
        for (Block block : content) {
          contentStream.beginText();
          setTextPropertiesAddText(contentStream, block);
          contentStream.endText();
        }
      }
      return createTempFile(document, "receipt-");
    } catch (IOException e) {
      throw new TechnicalException(
          HttpStatus.INTERNAL_SERVER_ERROR, TechnicalException.ERROR_CREATING_PDF);
    }
  }

  private List<Block> createReceiptPageContent(
      PDDocument document, TransferReceiptContext receiptDto) throws IOException {
    PDFont defaultFont;
    PDFont boldFont;
    try (InputStream defaultFontStream =
            new ClassPathResource("fonts/Roboto-Regular.ttf").getInputStream();
        InputStream boldFontStream =
            new ClassPathResource("fonts/Roboto-Bold.ttf").getInputStream()) {
      defaultFont = PDType0Font.load(document, defaultFontStream);
      boldFont = PDType0Font.load(document, boldFontStream);
    }
    final String header = "Etalon Bank";
    final String footer1 = "This receipt has an informational purpose only, no signature";
    final String footer2 = "is required.";
    final int offset = 20; // default in-between-block offset
    int currentVerticalOffset = 390;
    List<Block> result = new ArrayList<>();
    // header
    result.add(
        new Block(
            header, boldFont, 18, new Color(0.46f, 0.73f, 0.62f), 120, currentVerticalOffset));

    // main block
    currentVerticalOffset =
        addMainBlocksAndReturnVerticalOffset(
            defaultFont,
            result,
            getReceiptBlock(receiptDto),
            currentVerticalOffset - offset,
            offset);
    currentVerticalOffset =
        addMainBlocksAndReturnVerticalOffset(
            defaultFont, result, getSenderBlock(receiptDto), currentVerticalOffset, offset);
    currentVerticalOffset =
        addMainBlocksAndReturnVerticalOffset(
            defaultFont, result, getBeneficiaryBlock(receiptDto), currentVerticalOffset, offset);
    currentVerticalOffset =
        addMainBlocksAndReturnVerticalOffset(
            defaultFont, result, getAmountBlock(receiptDto), currentVerticalOffset, offset);

    // footer
    final Color footerColor = new Color(0.53f, 0.53f, 0.54f);
    currentVerticalOffset -= offset;
    result.add(new Block(footer1, defaultFont, 10, footerColor, 50, currentVerticalOffset));
    result.add(new Block(footer2, defaultFont, 10, footerColor, 160, currentVerticalOffset - 10));
    return result;
  }

  private void setTextPropertiesAddText(PDPageContentStream contentStream, Block block)
      throws IOException {
    contentStream.setFont(block.font(), block.fontSize());
    contentStream.setNonStrokingColor(block.color());
    setPositionAddText(contentStream, block.tx(), block.ty(), block.text());
  }

  private int addMainBlocksAndReturnVerticalOffset(
      PDFont defaultFont,
      final List<Block> blocks,
      final String[][] textBlock,
      int ty,
      final int offset) {
    final Color[] colorMatrix =
        new Color[] {new Color(0.53f, 0.53f, 0.54f), new Color(0.22f, 0.24f, 0.25f)};
    for (String[] line : textBlock) {
      ty -= offset;
      for (int i = 0; i < line.length; i++) {
        blocks.add(
            new Block(
                line[i],
                defaultFont,
                12,
                colorMatrix[i],
                (i == 0) ? 20 : ROWS_VERTICAL_OFFSET[blocks.size() / 2 - 1],
                ty));
      }
    }
    return ty - offset;
  }

  private String[][] getReceiptBlock(TransferReceiptContext receiptDto) {
    return new String[][] {{"Transfer receipt number", receiptDto.receiptNumber()}};
  }

  private String[][] getSenderBlock(TransferReceiptContext receiptDto) {
    return new String[][] {
      {"Sender  ", receiptDto.senderFullName()},
      {"Sent from  ", receiptDto.senderAccountNumber()},
      {
        "Transfer date  ",
        receiptDto.transferDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm"))
      },
      {"Payer`s bank  ", "Etalon Bank"},
      {"Description  ", receiptDto.description()},
    };
  }

  private String[][] getBeneficiaryBlock(TransferReceiptContext receiptDto) {
    return new String[][] {
      {"Beneficiary  ", receiptDto.beneficiaryFullName()},
      {"Account number  ", receiptDto.beneficiaryAccountNumber()},
      {"Payee`s bank  ", receiptDto.beneficiaryBank()},
    };
  }

  private String[][] getAmountBlock(TransferReceiptContext receiptDto) {
    return new String[][] {
      {"Total transfer amount  ", receiptDto.totalAmount().toString() + " " + receiptDto.currency()}
    };
  }

  private record Block(String text, PDFont font, float fontSize, Color color, int tx, int ty) {}
}
