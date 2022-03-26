package com.example.findteam_android_v10;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.findteam_android_v10.adapters.detailMyProjectAdapter;
import com.example.findteam_android_v10.classes.UserProject;

import java.util.ArrayList;

public class DetailMyProjectActivity extends AppCompatActivity {
    RecyclerView rvMembers;
    String[] memberNames = {"Ikemen Kuma", "Đạt Manacup", "Rory Eckel", "Samantha Holmes"};
    TextView tvDescription;
    TextView tvProjectStatus;
    ImageView ivStatus;
    ImageView ivGitHub;
    ImageView ivOneDrive;

    ArrayList<UserProject> members;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_my_project);
        this.rvMembers = (RecyclerView) findViewById(R.id.rvMembers);
        this.tvDescription = (TextView) findViewById(R.id.tvDescriptionMyDetailProjects);
        this.tvProjectStatus = (TextView) findViewById(R.id.tvStatusMyProjectDetailMain);
        this.ivGitHub = (ImageView) findViewById(R.id.ivGitHubMyDetailProjects) ;
        this.ivStatus = (ImageView) findViewById(R.id.ivStatusMyProjectDetail) ;
        this.ivOneDrive = (ImageView) findViewById(R.id.ivOneDriveMyDetailProjects);

//        members = UserProject.creatUserProjectsList(4);
//        members.get(0).setUserName(memberNames[0]);
//        members.get(0).setRole("Owner");
//        members.get(1).setUserName(memberNames[1]);
//        members.get(2).setUserName(memberNames[2]);
//        members.get(3).setUserName(memberNames[3]);

        final detailMyProjectAdapter adapter = new detailMyProjectAdapter(members);
        this.rvMembers.setAdapter(adapter);
        this.rvMembers.setLayoutManager(new LinearLayoutManager(this));
//        this.rvMembers.notifyDataSetChanged();

        this.tvDescription.setText("Software.Enterprises, a\n" +
                "         subsidiary of Silicon Spectra, is one\n" +
                "         of the leading providers of Enterprise so\n" +
                "         ftware solutions, comprehensive IT solutions\n" +
                "         , including Systems Integration, Consulting,\n" +
                "         Information Systems outsourcing, IT-enabled\n" +
                "          services, telecommunication, RD etc. We spe\n" +
                "          cialize in placement of UI Developer\n" +
                "          s, ReactJS, AngularJS, Quality Assurance Analyst, Data ");
        this.tvProjectStatus.setText("In Progress");

        this.ivGitHub.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse("https://github.com/DroidTech-TTU/findteam_android_v1.0"));
                startActivity(intent);
            }
        });

        this.ivOneDrive.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse("https://texastechuniversity-my.sharepoint.com/personal/rory_eckel_ttu_edu/_layouts/15/onedrive.aspx?id=%2Fpersonal%2Frory%5Feckel%5Fttu%5Fedu%2FDocuments%2F4%20Senior%20Year%2FCS%204366&FolderCTID=0x012000E8796DCDDF83224DAAB4C5519E7FB231"));
                startActivity(intent);
            }
        });
    }
}