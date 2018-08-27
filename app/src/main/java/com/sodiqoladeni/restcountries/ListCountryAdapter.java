package com.sodiqoladeni.restcountries;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class ListCountryAdapter extends RecyclerView.Adapter<ListCountryAdapter.ViewHolder>{

    private List<CountryListModel> countryData;
    private Context context;

    public ListCountryAdapter(Context context, List<CountryListModel> countries) {
        this.context = context;
        this.countryData = countries;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(context).
                                inflate(R.layout.model_list_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.countryFirstLetter.setText(countryData.get(i).getNameInitialChar());
        viewHolder.countryName.setText(countryData.get(i).getName());
        viewHolder.countryCurrency.setText(countryData.get(i).getCurrency());
        viewHolder.countryLanguage.setText(countryData.get(i).getLanguage());

        // Set the proper background color on the magnitude circle.
        // Fetch the background from the TextView, which is a GradientDrawable.
        GradientDrawable initialCircle = (GradientDrawable) viewHolder.countryFirstLetter.getBackground();
        // Get the appropriate background color based on the current country magnitude
        int magnitudeColor = getMagnitudeColor(context, countryData.get(i).getNameInitial());
        // Set the color on the magnitude circle
        initialCircle.setColor(magnitudeColor);
    }

    @Override
    public int getItemCount() {
        return countryData.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        private TextView countryFirstLetter, countryName, countryCurrency, countryLanguage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            countryFirstLetter = itemView.findViewById(R.id.first_initial);
            countryName = itemView.findViewById(R.id.country_name);
            countryCurrency = itemView.findViewById(R.id.country_currency);
            countryLanguage = itemView.findViewById(R.id.country_language);
        }
    }


    /**
     * Return the color for the magnitude circle based on the intensity of the country.
     *
     * @param magnitude of the country data
     */
    private int getMagnitudeColor(Context context, double magnitude) {
        int magnitudeColorResourceId;
        int magnitudeFloor = (int) Math.floor(magnitude);
        switch (magnitudeFloor) {
            case 0:
            case 1:
                magnitudeColorResourceId = R.color.magnitude1;
                break;
            case 2:
                magnitudeColorResourceId = R.color.magnitude2;
                break;
            case 3:
                magnitudeColorResourceId = R.color.magnitude3;
                break;
            case 4:
                magnitudeColorResourceId = R.color.magnitude4;
                break;
            case 5:
                magnitudeColorResourceId = R.color.magnitude5;
                break;
            case 6:
                magnitudeColorResourceId = R.color.magnitude6;
                break;
            case 7:
                magnitudeColorResourceId = R.color.magnitude7;
                break;
            case 8:
                magnitudeColorResourceId = R.color.magnitude8;
                break;
            case 9:
                magnitudeColorResourceId = R.color.magnitude9;
                break;
            default:
                magnitudeColorResourceId = R.color.magnitude10plus;
                break;
        }

        return ContextCompat.getColor(context, magnitudeColorResourceId);
    }

    public void addAll(List<CountryListModel> searchResult) {
        for (CountryListModel result : searchResult) {
            add(result);
        }
    }

    public void add(CountryListModel r) {
        countryData.add(r);
        notifyItemInserted(countryData.size() - 1);
    }

    public void removeItem(int position) {
        countryData.remove(position);
        notifyItemRemoved(position);
    }

    public void clear() {
        countryData.clear();
        notifyDataSetChanged();
    }
}
