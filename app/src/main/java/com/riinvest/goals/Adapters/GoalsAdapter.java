package com.riinvest.goals.Adapters;

import android.content.Context;
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

public class GoalsAdapter extends RecyclerView.Adapter<GoalsAdapter.ViewHolder> {

    private List<GoalModel> goalList;
    private DatabaseHandler db;
    private AddGoalActivity activity;

    public GoalsAdapter(DatabaseHandler db, AddGoalActivity activity) {
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

    public void deleteItem(int position) {
        GoalModel item = goalList.get(position);
        db.deleteGoal(item.getId());
        goalList.remove(position);
        notifyItemRemoved(position);
    }

    public void editItem(int position) {
        GoalModel item = goalList.get(position);
        Bundle bundle = new Bundle();
        bundle.putInt("id", item.getId());
        bundle.putString("task", item.getGoal());
        bundle.putString("startDate", item.getStartDate());
        bundle.putString("endDate", item.getEndDate());
        AddNewGoal fragment = new AddNewGoal();
        fragment.setArguments(bundle);
        fragment.show(activity.getSupportFragmentManager(), AddNewGoal.TAG);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView goal;
        TextView dates;

        ViewHolder(View view) {
            super(view);
            goal = view.findViewById(R.id.goal);
            dates = view.findViewById(R.id.dates);
        }
    }
}
