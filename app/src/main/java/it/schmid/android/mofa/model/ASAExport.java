package it.schmid.android.mofa.model;

import android.util.Xml;

import org.xmlpull.v1.XmlSerializer;

import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.List;

import it.schmid.android.mofa.db.DatabaseManager;

public class ASAExport {

    private final boolean isNewASA;
    private final SimpleDateFormat formatter;

    public ASAExport(boolean isNewASA) {
        this.isNewASA = isNewASA;
        this.formatter = new SimpleDateFormat();
        this.formatter.applyPattern("yyyy-MM-dd");
    }

    public String export(DatabaseManager storage) {
        return isNewASA ? createXMLASAVer16(storage) : createXMLASA(storage);
    }

    private String createXMLASA(DatabaseManager storage) {
        //List<Work> workUploadList = storage.getAllWorks();
        List<Work> workUploadList = storage.getAllValidNotSendedWorks();
        XmlSerializer serializer = Xml.newSerializer();
        StringWriter writer = new StringWriter();
        try {
            serializer.setOutput(writer);
            serializer.startDocument("UTF-8", true);
            serializer.startTag("", "Arbeitseintraege");
            //serializer.attribute("", "number", String.valueOf(workUploadList.size()));
            for (Work wk : workUploadList) {
                serializer.startTag("", "Arbeitseintrag");
                serializer.startTag("", "Datum");
                serializer.text(formatter.format(wk.getDate()));
                serializer.endTag("", "Datum");
                serializer.startTag("", "Arbeit");
                serializer.startTag("", "Code");
                serializer.text(wk.getTask().getCode());
                serializer.endTag("", "Code");
                serializer.endTag("", "Arbeit");

                serializer.startTag("", "Notiz");
                String note = wk.getNote();
                serializer.text(note);
                serializer.endTag("", "Notiz");
                List<WorkWorker> workers = storage.getWorkWorkerByWorkId(wk.getId());

                for (WorkWorker w : workers) {
                    serializer.startTag("", "Arbeitskraft");
                    serializer.startTag("", "Arbeitskraft");
                    serializer.startTag("", "Code");
                    Worker worker = storage.getWorkerWithId(w.getWorker().getId());
                    serializer.text(worker.getCode());
                    serializer.endTag("", "Code");
                    serializer.endTag("", "Arbeitskraft");
                    serializer.startTag("", "Stunden");
                    serializer.text(w.getHours().toString());
                    serializer.endTag("", "Stunden");

                    serializer.endTag("", "Arbeitskraft");
                }


                List<WorkMachine> machines = storage.getWorkMachineByWorkId(wk.getId());

                for (WorkMachine m : machines) {
                    serializer.startTag("", "Maschine");
                    serializer.startTag("", "Maschine");
                    serializer.startTag("", "Code");
                    Machine machine = storage.getMachineWithId(m.getMachine().getId());
                    serializer.text(machine.getCode());
                    serializer.endTag("", "Code");
                    serializer.endTag("", "Maschine");
                    serializer.startTag("", "Stunden");
                    serializer.text(m.getHours().toString());
                    serializer.endTag("", "Stunden");
                    serializer.endTag("", "Maschine");
                }

                List<WorkVQuarter> vquarters = storage.getVQuarterByWorkId(wk.getId());

                for (WorkVQuarter vq : vquarters) {
                    serializer.startTag("", "Sortenquartier");
                    serializer.startTag("", "Sortenquartier");
                    serializer.startTag("", "Code");
                    VQuarter vquarter = storage.getVQuarterWithId(vq.getVquarter().getId());
                    serializer.text(vquarter.getCode());
                    serializer.endTag("", "Code");
                    serializer.endTag("", "Sortenquartier");
                    serializer.endTag("", "Sortenquartier");
                }

                serializer.endTag("", "Arbeitseintrag");
            }
            serializer.endTag("", "Arbeitseintraege");
            serializer.endDocument();
            return writer.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String createXMLASAVer16(DatabaseManager storage) {
        //List<Work> workUploadList = storage.getAllWorks();
        List<Work> workUploadList = storage.getAllValidNotSendedWorks();
        XmlSerializer serializer = Xml.newSerializer();
        StringWriter writer = new StringWriter();
        try {
            serializer.setOutput(writer);
            serializer.startDocument("UTF-8", true);
            serializer.startTag("", "Arbeitseintraege");
            //serializer.attribute("", "number", String.valueOf(workUploadList.size()));
            for (Work wk : workUploadList) {
                serializer.startTag("", "Arbeitseintrag");
                serializer.startTag("", "Datum");
                serializer.text(formatter.format(wk.getDate()));
                serializer.endTag("", "Datum");
                serializer.startTag("", "Arbeit");
                serializer.startTag("", "Code");
                serializer.text(wk.getTask().getCode());
                serializer.endTag("", "Code");
                serializer.endTag("", "Arbeit");

                serializer.startTag("", "Notiz");
                String note = wk.getNote();
                serializer.text(note);
                serializer.endTag("", "Notiz");
                List<WorkWorker> workers = storage.getWorkWorkerByWorkId(wk.getId());

                for (WorkWorker w : workers) {
                    serializer.startTag("", "Arbeitskraft");
                    serializer.startTag("", "Arbeitskraft");
                    serializer.startTag("", "Code");
                    Worker worker = storage.getWorkerWithId(w.getWorker().getId());
                    serializer.text(worker.getCode());
                    serializer.endTag("", "Code");
                    serializer.endTag("", "Arbeitskraft");
                    serializer.startTag("", "Stunden");
                    serializer.text(w.getHours().toString());
                    serializer.endTag("", "Stunden");

                    serializer.endTag("", "Arbeitskraft");
                }


                List<WorkMachine> machines = storage.getWorkMachineByWorkId(wk.getId());

                for (WorkMachine m : machines) {
                    serializer.startTag("", "Maschine");
                    serializer.startTag("", "Maschine");
                    serializer.startTag("", "Code");
                    Machine machine = storage.getMachineWithId(m.getMachine().getId());
                    serializer.text(machine.getCode());
                    serializer.endTag("", "Code");
                    serializer.endTag("", "Maschine");
                    serializer.startTag("", "Stunden");
                    serializer.text(m.getHours().toString());
                    serializer.endTag("", "Stunden");
                    serializer.endTag("", "Maschine");
                }

                List<WorkVQuarter> vquarters = storage.getVQuarterByWorkId(wk.getId());

                for (WorkVQuarter vq : vquarters) {
                    serializer.startTag("", "Sortenquartier");
                    serializer.startTag("", "Sortenquartier");
                    serializer.startTag("", "Code");
                    VQuarter vquarter = storage.getVQuarterWithId(vq.getVquarter().getId());
                    serializer.text(vquarter.getCode());
                    serializer.endTag("", "Code");
                    serializer.endTag("", "Sortenquartier");
                    serializer.endTag("", "Sortenquartier");
                }
                serializer.endTag("", "Arbeitseintrag");
            }
            serializer.endTag("", "Arbeitseintraege");
            serializer.endDocument();
            return writer.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}