package com.example.assets.AdminActivity;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.documentfile.provider.DocumentFile;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.text.format.DateFormat;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.assets.Adapter.CustomListViewReportAdapter;
import com.example.assets.AlterDialog.MessageDialog;
import com.example.assets.BuildConfig;
import com.example.assets.MainActivity;
import com.example.assets.Model.Report;
import com.example.assets.R;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lib.folderpicker.FolderPicker;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReportActivity extends AppCompatActivity {
    ListView listView;
    List<Report> reports;
    CustomListViewReportAdapter customListViewReportAdapter;
    ImageView saveButton,backAsset;
    private static final int PICKFILE_REQUEST_CODE = 100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        listView=findViewById(R.id.listViewReport);
        saveButton=findViewById(R.id.ic_save);
        reports= new ArrayList<>();
        customListViewReportAdapter= new CustomListViewReportAdapter(getApplicationContext(),reports);
        listView.setAdapter(customListViewReportAdapter);
        backAsset=findViewById(R.id.backAsset);
        backAsset.setOnClickListener(v->finish());
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MessageDialog.getInstance(ReportActivity.this,"Are you sure?","Do you want to export data?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(ReportActivity.this, FolderPicker.class);
                        startActivityForResult(intent, PICKFILE_REQUEST_CODE);
                    }
                }).setNegativeButton("No",null).show();
            }
        });
        loadReport();
    }
    public void loadReport()
    {
        MainActivity.service.getReport().enqueue(new Callback<List<Report>>() {
            @Override
            public void onResponse(Call<List<Report>> call, Response<List<Report>> response) {
                if(response.code()==200)
                {
                    reports.clear();
                    reports.addAll(response.body());
                    customListViewReportAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<Report>> call, Throwable t) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if(requestCode == PICKFILE_REQUEST_CODE && resultCode == Activity.RESULT_OK)
        {
            String folderLocation = intent.getExtras().getString("data");
//            SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");
            SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy-HH-mm-ss");
            Date date= new Date();
            Workbook wb = createExcel();
            File file = new File(folderLocation ,"asset_management_"+dateFormatter.format(date)+".xls");
            FileOutputStream outputStream = null;
            try {
                outputStream = new FileOutputStream(file);
                wb.write(outputStream);
                MessageDialog.getInstance(ReportActivity.this,"Success","Export data success\nPath: "+file.getPath()).setPositiveButton("Open", new DialogInterface.OnClickListener() {
                    @SuppressLint("IntentReset")
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        Uri uri =FileProvider.getUriForFile(ReportActivity.this, BuildConfig.APPLICATION_ID + ".provider",file);
                        Uri uri=Uri.fromFile(file);
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(file.getPath()));
                        intent.setType(get_mime_type(uri.toString()));
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(Intent.createChooser(intent, "Open file with"));

                    }
                }).setNegativeButton("Cancel",null).show();
            }catch (IOException e)
            {
                e.printStackTrace();
                try {
                    outputStream.close();
                    MessageDialog.getInstance(ReportActivity.this,"Error","Something went wrong").show();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        }

    }
    public String get_mime_type(String url) {
        String ext = MimeTypeMap.getFileExtensionFromUrl(url);
        String mime = null;
        if (ext != null) {
            mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext);
        }
        return mime;
    }
    private Workbook createExcel()
    {
        Workbook wb = new HSSFWorkbook();
        Cell cell= null;
        CellStyle cellStyle =wb.createCellStyle();
        cellStyle.setFillBackgroundColor(HSSFColor.LIGHT_BLUE.index);

        //
        Sheet sheet=null;
        sheet =wb.createSheet("Data");
        Row row = sheet.createRow(0);
        cell =row.createCell(0);
        cell.setCellValue("ASSET MANAGEMENT REPORT");
        cell.setCellStyle(cellStyle);
        cell =row.createCell(1);
        cell.setCellValue(DateFormat.format("dd/MM/yyyy", new Date()).toString());
        cell.setCellStyle(cellStyle);

        row = sheet.createRow(1);
        cell=row.createCell(0);
        cell.setCellValue("Category");
        cell.setCellStyle(cellStyle);

        cell=row.createCell(1);
        cell.setCellValue("Total");
        cell.setCellStyle(cellStyle);

        cell=row.createCell(2);
        cell.setCellValue("Assigned");
        cell.setCellStyle(cellStyle);

        cell=row.createCell(3);
        cell.setCellValue("Available");
        cell.setCellStyle(cellStyle);

        cell=row.createCell(4);
        cell.setCellValue("Not available");
        cell.setCellStyle(cellStyle);

        cell=row.createCell(5);
        cell.setCellValue("Waiting for recycling");
        cell.setCellStyle(cellStyle);

        cell=row.createCell(6);
        cell.setCellValue("Recycled");
        cell.setCellStyle(cellStyle);


        for(int i=2;i<=reports.size()+1;i++)
        {
            row = sheet.createRow(i);
            //
            cell=row.createCell(0);
            cell.setCellValue(reports.get(i-2).getCategory());
            cell.setCellStyle(cellStyle);

            cell=row.createCell(1);
            cell.setCellValue(reports.get(i-2).getTotal());
            cell.setCellStyle(cellStyle);

            cell=row.createCell(2);
            cell.setCellValue(reports.get(i-2).getAssigned());
            cell.setCellStyle(cellStyle);

            cell=row.createCell(3);
            cell.setCellValue(reports.get(i-2).getAvailable());
            cell.setCellStyle(cellStyle);

            cell=row.createCell(4);
            cell.setCellValue(reports.get(i-2).getNotAvailable());
            cell.setCellStyle(cellStyle);

            cell=row.createCell(5);
            cell.setCellValue(reports.get(i-2).getWaitingForRecycle());
            cell.setCellStyle(cellStyle);

            cell=row.createCell(6);
            cell.setCellValue(reports.get(i-2).getRecycled());
            cell.setCellStyle(cellStyle);
        }
        sheet.setColumnWidth(0,(10*200));
        sheet.setColumnWidth(1,(10*200));
        sheet.setColumnWidth(2,(10*200));
        sheet.setColumnWidth(3,(10*200));
        sheet.setColumnWidth(4,(10*200));
        sheet.setColumnWidth(5,(10*200));
        sheet.setColumnWidth(6,(10*200));
        return wb;
    }
}