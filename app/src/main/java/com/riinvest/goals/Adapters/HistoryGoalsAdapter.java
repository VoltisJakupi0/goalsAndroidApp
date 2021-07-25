package com.riinvest.goals.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.riinvest.goals.AddGoalActivity;
import com.riinvest.goals.AddNewGoal;
import com.riinvest.goals.HistoryActivity;
import com.riinvest.goals.Model.GoalModel;

import com.riinvest.goals.R;
import com.riinvest.goals.Utils.DatabaseHandler;

import java.util.List;

public class HistoryGoalsAdapter extends RecyclerView.Adapter<HistoryGoalsAdapter.ViewHolder> {

    private List<GoalModel> goalList;
    private DatabaseHandler db;
    private HistoryActivity activity;

    public HistoryGoalsAdapter(DatabaseHandler db, HistoryActivity activity) {
        this.db = db;
        this.activity = activity;
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.task_layout, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        db.openDatabase();

        final GoalModel item = goalList.get(position);
        holder.goal.setText(item.getGoal());
        holder.dates.setText(item.getStartDate() + " - " + item.getEndDate());
        holder.status.setVisibility(View.VISIBLE);
        holder.status.setText(item.getStatus());
        holder.status.setTextColor(item.getStatus().equals("FINISHED") ? Color.parseColor("#5BAD55"): Color.parseColor("#C52F1A"));

    }

    private boolean toBoolean(int n) {
        return n != 0;
    }

    @Override
    public int getItemCount() {
        return goalList.size();
    }

    public Context getContext() {
        return activity;
    }

    public void setGoals(List<GoalModel> goalList) {
        this.goalList = goalList;
        notifyDataSetChanged();
    }



    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView goal;
        TextView dates;
        TextView status;


        ViewHolder(View view) {
            super(view);
            goal = view.findViewById(R.id.goal);
            dates = view.findViewById(R.id.dates);
            status = view.findViewById(R.id.status);
        }
    }
}
