package redsli.me.wafflebot.util;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created by redslime on 13.04.2018
 */
public class SSHelper {

    public static final Sheets SERVICE = GoogleSheets.getSheetsService();
    public static final String SHEET_ID = "1CrQOxzaXC6iSjwZwQvu6DNIYsCDg-uQ4x5UiaWLHzxg";
    public static final String RANGE = "Upcoming Matches!B10:T60";
    public static final int ROW_DATE = 0;
    public static final int ROW_DAY = 1;
    public static final int COLUMN_TIME = 0;
    public static final int MAX_ROW = 50;
    public static final int MAX_COLUMN = 18;

    private static ValueRange vr;
    private static long lastCache;

    public static ValueRange getValueRange() throws IOException {
        if(lastCache == 0 || System.currentTimeMillis() - lastCache > TimeUnit.MINUTES.toMillis(1)) {
            vr = SERVICE.spreadsheets().values().get(SHEET_ID, RANGE).execute();
            lastCache = System.currentTimeMillis();
        }
        return vr;
    }

    public static List<Cell> getAllCells() throws IOException {
        List<Cell> cells = new ArrayList<>();
        List<List<Object>> data = getValueRange().getValues();
        for(int rowIndex = 0; rowIndex < MAX_ROW; rowIndex++) {
            for(int columnIndex = 0; columnIndex < MAX_COLUMN; columnIndex++) {
                if(data.size() > rowIndex) {
                    if(data.get(rowIndex).size() > columnIndex) {
                        String str = (String) data.get(rowIndex).get(columnIndex);
                        cells.add(new Cell(rowIndex, columnIndex, str));
                    } else
                        cells.add(new Cell(rowIndex, columnIndex, ""));
                } else
                    cells.add(new Cell(rowIndex, columnIndex, ""));
            }
        }
        return cells;
    }

    public static List<Cell> getAllInnerCells() throws IOException {
        return getAllCells().stream().filter(c -> c.row != ROW_DATE && c.row != ROW_DAY && c.column != COLUMN_TIME).collect(Collectors.toList());
    }

    public static List<Cell> getRow(int index) throws IOException {
        return getAllCells().stream().filter(c -> c.row == index).collect(Collectors.toList());
    }

    public static List<Cell> getColumn(int index) throws IOException {
        return getAllCells().stream().filter(c -> c.column == index).collect(Collectors.toList());
    }

    public static Cell getCell(int row, int column) throws IOException {
        return getAllCells().stream().filter(c -> c.row == row && c.column == column).findFirst().orElse(null);
    }

    public static List<Match> getMatches() throws IOException, ParseException {
        List<Match> matches = new ArrayList<>();
        for(Cell cell : getAllInnerCells()) {
            if(cell.string == null || cell.string.trim().equals("") || cell.string.trim().equals("^"))
                continue;

            Cell end = cell.getRelative(Direction.DOWN);
            while (end != null && end.string != null && end.string.trim().equals("^")) {
                if(end.getRelative(Direction.DOWN) == null)
                    break;
                end = end.getRelative(Direction.DOWN);
            }
            if(end != null) {
                matches.add(new Match(cell, cell.string, cell.getTimeEST(), end.getTimeEST()));
            }
        }
        matches.sort((o1, o2) -> {
            if(o1.begin.toInstant().isBefore(o2.begin.toInstant())) return -1;
            if(o1.begin.toInstant().isAfter(o2.begin.toInstant())) return 1;
            return 0;
        });
        return matches;
    }

    public static Match getNow() throws IOException, ParseException {
        Date now = Date.from(Instant.now());
        return getMatches().stream().filter(m -> m.begin.getTime() <= now.getTime() && m.end.getTime() >= now.getTime()).findFirst().orElse(null);
    }

    public static Match getNext() throws IOException, ParseException {
        return getMatches().stream().filter(m -> m.begin.getTime() >= Date.from(Instant.now()).getTime()).findFirst().orElse(null);
    }

    public static class Cell {
        public int row;
        public int column;
        public String string;

        Cell(int row, int column, String string) throws IOException {
            this.row = row;
            this.column = column;
            this.string = string;
        }

        public String getTime() throws IOException {
            return getCell(row, COLUMN_TIME).string;
        }

        public String getDate() throws IOException {
            return getCell(ROW_DATE, column).string;
        }

        public String getDay() throws IOException {
            return getCell(ROW_DAY, column).string;
        }

        public String getTimeEST() throws IOException {
            return getTime().replaceAll("(.*) EST.*", "$1");
        }

        @Override
        public String toString() {
            try {
                return "Cell[row=" + row + ", column=" + column + ", string=" + string + ", time=" + getTime() + ", date=" + getDate() + ", day=" + getDay() + ", est=" + getTimeEST() + "]";
            } catch (IOException e) {
                return "Cell[row=" + row + ", column=" + column + ", string=" + string + "]";
            }
        }

        @Override
        public boolean equals(Object obj) {
            if(obj instanceof Cell) {
                Cell c = (Cell) obj;
                return c.row == row && c.column == column && c.string == string;
            }
            return false;
        }

        public Cell getRelative(Direction direction) throws IOException {
            switch (direction) {
                case UP: {
                    return getCell(row - 1, column);
                }

                case DOWN: {
                    return getCell(row + 1, column);
                }

                case LEFT: {
                    return getCell(row, column - 1);
                }

                case RIGHT: {
                    return getCell(row, column + 1);
                }
            }
            return null;
        }
    }

    public static class Match {
        public Cell parent;
        public String name;
        public Date begin;
        public Date end;

        public Match(Cell parent, String name, String begin, String end) throws IOException, ParseException {
            this.parent = parent;
            this.name = name;

            // input= M/D/YYYY h:mma <=> 4/7/2018 4:30pm
            for(int i = 0; i < 2; i++) {
                String input = parent.getDate() + " " + (i == 0 ? begin.replace("pm", "PM").replace("am", "AM") : end.replace("pm", "PM").replace("am", "AM"));
                SimpleDateFormat parser = new SimpleDateFormat("M/d/yyyy h:mma");
                parser.setTimeZone(TimeZone.getTimeZone("EST"));
                if(i == 0) this.begin = parser.parse(input);
                else this.end = parser.parse(input);
            }
        }

        @Override
        public String toString() {
            return "Match[parent=" + parent.toString() + ", name=" + name + ", begin=" + begin + ", end= " + end + "]";
        }
    }

    public enum Direction {
        UP, DOWN, LEFT, RIGHT
    }
}