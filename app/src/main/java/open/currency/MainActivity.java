/*
 *     OpenCurrency, a very simple currency converter for Android
 *
 *     Copyright (C) 2015  Jan-Lukas Else (janlukas.else@gmail.com)
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package open.currency;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.afollestad.async.Action;
import com.afollestad.bridge.Bridge;

import org.json.JSONObject;

import java.util.ArrayList;

import jlelse.simpleui.SimpleActivity;

public class MainActivity extends SimpleActivity {

    private EditText currency1, currency2;
    private TextInputLayout tilCur1, tilCur2;
    private int cur1, cur2;
    private String[] currencyArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setToolbarEnabled(true);

        currencyArray = getResources().getStringArray(R.array.currencies);

        currency1 = (EditText) findViewById(R.id.currency1);
        currency2 = (EditText) findViewById(R.id.currency2);

        tilCur1 = (TextInputLayout) findViewById(R.id.tilCur1);
        tilCur2 = (TextInputLayout) findViewById(R.id.tilCur2);

        Spinner spinner1 = (Spinner) findViewById(R.id.spinner1);
        Spinner spinner2 = (Spinner) findViewById(R.id.spinner2);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.currencies, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner1.setAdapter(adapter);
        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                cur1 = position;
                tilCur1.setHint(currencyArray[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        spinner2.setAdapter(adapter);
        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                cur2 = position;
                tilCur2.setHint(currencyArray[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        setFabEnabled(true);
        setFabDrawable(ContextCompat.getDrawable(this, R.drawable.ic_arrow_forward_white_48dp));
        setFabListener(new View.OnClickListener() {

            Double c1value = Double.valueOf("0");

            @Override
            public void onClick(View v) {
                try {
                    c1value = Double.valueOf(currency1.getText().toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (c1value.equals(Double.valueOf("0"))) {
                    Snackbar.make(v, R.string.empty_value, Snackbar.LENGTH_LONG).show();
                } else if (cur1 == cur2) {
                    Snackbar.make(v, R.string.sam_cur, Snackbar.LENGTH_LONG).show();
                } else {
                    Action<Double> doCalculate = new Action<Double>() {
                        @NonNull
                        @Override
                        public String id() {
                            return "calculate";
                        }

                        @Nullable
                        @Override
                        protected Double run() throws InterruptedException {
                            return calculate(currencyArray[cur1], currencyArray[cur2], c1value);
                        }

                        @Override
                        protected void done(@Nullable Double result) {
                            if (result != null) {
                                currency2.setText(String.valueOf(result));
                            }
                        }
                    };
                    doCalculate.execute();
                }
            }
        });
    }

    private double calculate(String currency, String desiredCurrency, double value) {
        double returnValue = Double.valueOf("0");
        try {
            JSONObject jsonObject = Bridge.get("http://api.fixer.io/latest?base=%s&symbols=%s", currency, desiredCurrency).asJsonObject();
            if (jsonObject != null) {
                jsonObject = jsonObject.getJSONObject("rates");
                returnValue = jsonObject.getDouble(desiredCurrency) * value;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return returnValue;
    }

    private ArrayList<Integer> getHistory() {
        ArrayList<Integer> allValues = new ArrayList<>(20);
        return null;
    }
}
