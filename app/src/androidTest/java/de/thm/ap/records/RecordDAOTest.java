package de.thm.ap.records;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import de.thm.ap.records.model.Record;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class RecordDAOTest {

   private RecordDAO recordDAO;

   @Before
   public void setUp() throws Exception {
      Context ctx = InstrumentationRegistry.getContext();

      recordDAO = new RecordDAO(ctx);
      recordDAO.persist(new Record("CS1013", "Objektorientierte Programmierung", 2016, true, true, 6, 73));
      recordDAO.persist(new Record("MN1007", "Diskrete Mathematik", 2016, false, true, 6, 81));
      recordDAO.persist(new Record("CS1019", "Compilerbau", 2017, false, false, 6, 81));
      recordDAO.persist(new Record("CS1020", "Datenbanksysteme", 2017, false, false, 6, 92));
   }

   @Test
   public void findAll() throws Exception {
      List<Record> records = recordDAO.findAll();
      assertNotNull(records);
      assertEquals(4, recordDAO.findAll().size());
   }

   @Test
   public void findById() throws Exception {
      List<Record> records = recordDAO.findAll();
      assertNotNull(records);
      assertEquals(4, recordDAO.findAll().size());

      Collections.shuffle(records);
      Optional<Record> optRecord = recordDAO.findById(records.get(0).getId());

      assertTrue(optRecord.isPresent());
      assertEquals(records.get(0).getModuleNum(), optRecord.get().getModuleNum());
   }

   @Test
   public void update() throws Exception {
      String moduleNum = "MN1007";
      List<Record> records = recordDAO.findAll();
      assertNotNull(records);

      Optional<Record> optRecordOld = records.stream().filter(r -> moduleNum.equals(r.getModuleNum())).findFirst();
      assertTrue(optRecordOld.isPresent());

      Record record = optRecordOld.get();
      record.setModuleName("Diskrete Mathematik 123");
      record.setYear(2015);
      record.setSummerTerm(true);
      record.setHalfWeighted(false);
      record.setCrp(3);
      record.setMark(51);

      assertTrue(recordDAO.update(record));

      Optional<Record> optRecordUpdate = recordDAO.findById(record.getId());
      assertTrue(optRecordUpdate.isPresent());

      Record recordUpdate = optRecordUpdate.get();
      assertEquals(record.getModuleNum(), recordUpdate.getModuleNum());
      assertEquals(record.getModuleName(), recordUpdate.getModuleName());
      assertEquals(record.getYear(), recordUpdate.getYear());
      assertEquals(record.isSummerTerm(), recordUpdate.isSummerTerm());
      assertEquals(record.isHalfWeighted(), recordUpdate.isHalfWeighted());
      assertEquals(record.getCrp(), recordUpdate.getCrp());
      assertEquals(record.getMark(), recordUpdate.getMark());

      Record recordNoId = new Record("CS1016", "Programmierung interaktiver Systeme", 2016, true, true, 6, 95);
      assertFalse(recordDAO.update(recordNoId));
   }

   @Test
   public void persist() throws Exception {
      Record record = new Record("CS1022", "Betriebssysteme", 2017, false, false, 6, 90);
      Integer id = recordDAO.persist(record);

      Optional<Record> optRecord = recordDAO.findById(id);
      assertTrue(optRecord.isPresent());

      Record recordNew = optRecord.get();
      assertEquals(id, recordNew.getId());
      assertEquals(record.getModuleNum(), recordNew.getModuleNum());
      assertEquals(record.getModuleName(), recordNew.getModuleName());
      assertEquals(record.getYear(), recordNew.getYear());
      assertEquals(record.isSummerTerm(), recordNew.isSummerTerm());
      assertEquals(record.isHalfWeighted(), recordNew.isHalfWeighted());
      assertEquals(record.getCrp(), recordNew.getCrp());
      assertEquals(record.getMark(), recordNew.getMark());
   }

}