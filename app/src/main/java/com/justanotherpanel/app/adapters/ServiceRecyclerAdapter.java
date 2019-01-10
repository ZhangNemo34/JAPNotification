package com.justanotherpanel.app.adapters;

import android.support.v7.widget.RecyclerView;
import android.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.justanotherpanel.app.R;
import com.justanotherpanel.app.models.ServiceModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Set;

public class ServiceRecyclerAdapter extends RecyclerView.Adapter<ServiceRecyclerAdapter.ViewHolder> {
    private HashMap<Integer, ServiceModel> services;
    private Integer[] keys;

    public ServiceRecyclerAdapter(HashMap<Integer, ServiceModel> services) {
        this.services = services;
        this.keys = services.keySet().toArray(new Integer[services.size()]);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_service, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ServiceModel service = services.get(keys[position]);

        holder.txtServiceName.setText(service.getName());
        holder.txtServiceCategory.setText(service.getCategory());
        holder.txtServiceType.setText(service.getType());
        holder.txtServiceRate.setText(String.valueOf(service.getRate()));
        holder.txtServiceMin.setText(String.valueOf(service.getMin()));
        holder.txtServiceMax.setText(String.valueOf(service.getMax()));
    }

    @Override
    public int getItemCount() {
        return services.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtServiceName;
        TextView txtServiceCategory;
        TextView txtServiceType;
        TextView txtServiceRate;
        TextView txtServiceMin;
        TextView txtServiceMax;

        public ViewHolder(View itemView) {
            super(itemView);

            txtServiceName = itemView.findViewById(R.id.txtServiceName);
            txtServiceCategory = itemView.findViewById(R.id.txtServiceCategory);
            txtServiceType = itemView.findViewById(R.id.txtServiceType);
            txtServiceRate = itemView.findViewById(R.id.txtServiceRate);
            txtServiceMin = itemView.findViewById(R.id.txtServiceMin);
            txtServiceMax = itemView.findViewById(R.id.txtServiceMax);
        }
    }
}
