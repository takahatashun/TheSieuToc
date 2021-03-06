package me.lxc.thesieutoc.internal;

import me.lxc.thesieutoc.TheSieuToc;
import me.lxc.thesieutoc.handlers.InputCardHandler;
import org.json.simple.JSONObject;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class DornorLogElement {
    private final Date date;
    private final String playerName;
    private final InputCardHandler.LocalCardInfo cardInfo;
    private final boolean success;
    private final String notes;

    public DornorLogElement(Date date, String playerName, InputCardHandler.LocalCardInfo cardInfo, boolean success, String notes) {
        this.date = date;
        this.playerName = playerName;
        this.cardInfo = cardInfo;
        this.success = success;
        this.notes = notes;
    }

    public DornorLogElement(String player, InputCardHandler.LocalCardInfo cardInfo, boolean success, String notes) {
        this(new Date(), player, cardInfo, success, notes);
    }

    public Date getDate() {
        return date;
    }

    public String getPlayerName() {
        return playerName;
    }

    public InputCardHandler.LocalCardInfo getCardInfo() {
        return cardInfo;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getNotes() {
        return notes;
    }

    public String toString() {
        JSONObject json = new JSONObject();
        Map<String, Object> data = new HashMap<>();
        data.put("DATE", date.getTime());
        data.put("PLAYER", playerName);
        data.put("CARD", cardInfo.type + " | " + cardInfo.amount + " | " + cardInfo.serial + " | " + cardInfo.pin);
        data.put("SUCCESS", success);
        data.put("NOTES", notes);
        json.putAll(data);
        return json.toJSONString();
    }

    public static DornorLogElement parse(String line) {
        String[] data = line.split("[|]");
        SimpleDateFormat parser = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
        Date date = null;
        try {
            date = parser.parse(data[0]);
        } catch (ParseException e) {
            date = new Date(0);
        }
        String playerName = data[1].replace(" NAME ", "").trim();
        String serial = data[2].replace(" SERIAL ", "").trim();
        String pin = data[3].replace(" PIN ", "").trim();
        String type = data[4].replace(" TYPE ", "").trim();
        int amount = Integer.parseInt(data[5].replace(" AMOUNT ", "").trim());
        InputCardHandler.LocalCardInfo card = new InputCardHandler.LocalCardInfo(type, amount, serial, pin);
        boolean success = Boolean.parseBoolean(data[6].replace(" SUCCESS ", "").trim());
        String notes = data[7].replace(" NOTES ", "").trim();

        return new DornorLogElement(date, playerName, card, success, notes);
    }

    public static List<DornorLogElement> loadFromFile(File file) {
        List<DornorLogElement> logContent = Collections.emptyList();
        try (final BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            logContent = br.lines().map(DornorLogElement::parse).collect(Collectors.toList());
        } catch (IOException e) {
            TheSieuToc.pluginDebug.debug("Cannot load log from file...");
        }
        return logContent;
    }
}
