package apply;

import implement.HashMap;
import refactor.TreeMap;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

public class DaleDB implements StaticDaleDB {

    private final LinkedList<String> accessTracker = new LinkedList<>();
    private final HashMap<String, TreeMap<Long, DaleRecord>> pondMap = new HashMap<>();

    @Override
    public DaleRecord putRecord(DaleRecord daleRecord) {
        if (daleRecord == null) {
            throw new IllegalArgumentException("DaleRecord is Null");
        }
        if (!pondMap.containsKey(daleRecord.pond())) {
            pondMap.put(daleRecord.pond(), new TreeMap<>());
        }
        accessTracker.remove(daleRecord.pond());
        accessTracker.addLast(daleRecord.pond());
        return pondMap.get(daleRecord.pond()).put(daleRecord.timestamp(), daleRecord);
    }

    @Override
    public DaleRecord deleteRecord(String pond, long timestamp) {
        if (pond == null) {
            throw new IllegalArgumentException("Pond name is null");
        }
        DaleRecord returnVal = pondMap.get(pond).remove(timestamp);
        accessTracker.remove(pond);
        if (pondMap.get(pond).size() == 0) {
            pondMap.remove(pond);
        } else {
            accessTracker.addLast(pond);
        }
        return returnVal;
    }

    @Override
    public DaleRecord getRecord(String pond, long timestamp) {
        if (pond == null) {
            throw new IllegalArgumentException("Pond name is null");
        }
        if (!pondMap.containsKey(pond)) {
            throw new NoSuchElementException("Pond does not exist");
        }
        if (!pondMap.get(pond).containsKey(timestamp)) {
            throw new NoSuchElementException("Record does not exist in the pond");
        }
        accessTracker.remove(pond);
        accessTracker.addLast(pond);
        return pondMap.get(pond).get(timestamp);
    }

    @Override
    public List<DaleRecord> getPond(String pond) {
        if (pond == null) {
            throw new IllegalArgumentException("Pond name is null");
        }
        if (!pondMap.containsKey(pond)) {
            return List.of();
        }
        accessTracker.remove(pond);
        accessTracker.addLast(pond);
        return pondMap.get(pond).values();
    }

    @Override
    public int getPondCount() {
        return pondMap.size();
    }

    @Override
    public List<DaleRecord> getRecordRange(String pond, long start, long end) {
        if (pond == null) {
            throw new IllegalArgumentException("Pond name is null");
        }
        accessTracker.remove(pond);
        accessTracker.addLast(pond);
        return pondMap.get(pond).getRange(start, end);
    }

    @Override
    public List<DaleRecord> evict(int k) {
        if (k <= 0) {
            throw new IllegalArgumentException("K should be greater than 0");
        }
        if (k > getPondCount()) {
            throw new NoSuchElementException("K is larger than number of ponds");
        }
        List<DaleRecord> records = new LinkedList<>();
        for (int i = 0; i < k; i++) {
            String pond = accessTracker.removeFirst();
            records.addAll(pondMap.get(pond).values());
            pondMap.remove(pond);
        }
        return records;
    }

    @Override
    public int getPeakConcurrentOccupancy(String pond) {
        if (pond == null) {
            throw new IllegalArgumentException("Pond name is null");
        }
        TreeMap<Long, DaleRecord> currPond = pondMap.get(pond);
        Iterator<DaleRecord> pondIterator = currPond.iterator();
        int max = 0;
        int duckCount = 0;
        while (pondIterator.hasNext()) {
            DaleRecord curr = pondIterator.next();
            if (curr instanceof DaleRecord.BoundaryEvent) {
                int numCows = ((DaleRecord.BoundaryEvent) curr).numCows();
                int numNames = ((DaleRecord.BoundaryEvent) curr).names().size();
                if (((DaleRecord.BoundaryEvent) curr).type() == DaleRecord.BoundaryEvent.TransitionType.ENTRANCE) {
                    duckCount += numNames - numCows;
                } else {
                    duckCount -= numNames - numCows;
                }
                max = Math.max(max, duckCount);
            }
        }
        accessTracker.remove(pond);
        accessTracker.addLast(pond);
        return max;
    }

    @Override
    public String getMostFrequentVisitor(String pond) {
        if (pond == null) {
            throw new IllegalArgumentException("Pond name is null");
        }
        if (!pondMap.containsKey(pond)) {
            throw new NoSuchElementException("The pond is not in the database");
        }
        TreeMap<Long, DaleRecord> currPond = pondMap.get(pond);
        Iterator<DaleRecord> pondIterator = currPond.iterator();
        HashMap<String, Integer> nameCount = new HashMap<>();
        int highest = 0;
        String mostFrequent = null;
        while (pondIterator.hasNext()) {
            DaleRecord curr = pondIterator.next();
            if (curr instanceof DaleRecord.BoundaryEvent) {
                if (((DaleRecord.BoundaryEvent) curr).type() == DaleRecord.BoundaryEvent.TransitionType.ENTRANCE) {
                    List<String> names = ((DaleRecord.BoundaryEvent) curr).names();
                    for (String currName : names) {
                        if (!nameCount.containsKey(currName)) {
                            nameCount.put(currName, 1);
                        } else {
                            nameCount.put(currName, nameCount.get(currName) + 1);
                        }
                        if (nameCount.get(currName) > highest) {
                            highest = nameCount.get(currName);
                            mostFrequent = currName;
                        }
                    }
                }
            }
        }
        accessTracker.remove(pond);
        accessTracker.addLast(pond);
        return mostFrequent;
    }

    @Override
    public HashMap<Long, List<Long>> mergeReports(String pond) {
        if (pond == null) {
            throw new IllegalArgumentException("Pond name is null");
        }
        if (!pondMap.containsKey(pond)) {
            throw new NoSuchElementException("The pond is not in the database");
        }
        HashMap<String, LinkedList<DaleRecord.FishReport>> fishReports = new HashMap<>();
        TreeMap<Long, DaleRecord> currPond = pondMap.get(pond);
        for (DaleRecord curr : currPond) {
            if (curr instanceof DaleRecord.FishReport report) {
                if (!fishReports.containsKey(report.duck())) {
                    fishReports.put(report.duck(), new LinkedList<>());
                }
                fishReports.get(report.duck()).addLast(report);
            }
        }

        HashMap<Long, List<Long>> returnList = new HashMap<>();
        for (String duck : fishReports.keySet()) {
            LinkedList<DaleRecord.FishReport> reports = fishReports.get(duck);
            DaleRecord.FishReport latest = reports.getLast();
            List<Long> mergedTimestamps = new LinkedList<>();
            List<Double> mergedWeights = new LinkedList<>();
            for (int i = 0; i < reports.size() - 1; i++) {
                mergedTimestamps.add(reports.get(i).timestamp());
                mergedWeights.addAll(reports.get(i).weights());
                currPond.remove(reports.get(i).timestamp());
            }
            mergedWeights.addAll(latest.weights());
            currPond.remove(latest.timestamp());
            currPond.put(latest.timestamp(), new DaleRecord.FishReport(pond, latest.timestamp(), duck, mergedWeights));
            returnList.put(latest.timestamp(), mergedTimestamps);
        }
        accessTracker.remove(pond);
        accessTracker.addLast(pond);
        return returnList;
    }
}
