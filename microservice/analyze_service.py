from flask import Flask, jsonify
import pandas as pd
import os
import pprint
import json
from datetime import datetime
from flask_cors import CORS

app = Flask(__name__)
CORS(app, origins=["http://localhost:8080"])

UPLOAD_FOLDER = r'D:\InsightIQ\uploads'
SAVE_JSON_FOLDER = r'D:\InsightIQ\jsonFilesTesting'

@app.route('/analyze', methods=['GET'])
def analyze_files():
    try:
        os.makedirs(UPLOAD_FOLDER, exist_ok=True)
        os.makedirs(SAVE_JSON_FOLDER, exist_ok=True)

        files = sorted(
            [f for f in os.listdir(UPLOAD_FOLDER) if f.endswith(('.csv', '.xlsx'))],
            key=lambda x: os.path.getmtime(os.path.join(UPLOAD_FOLDER, x)),
            reverse=True
        )

        if len(files) < 2:
            return jsonify({"error": "Less than two files found"}), 400

        f1, f2 = files[1], files[0]
        p1, p2 = os.path.join(UPLOAD_FOLDER, f1), os.path.join(UPLOAD_FOLDER, f2)

        def read_df(path):
            return pd.read_excel(path, engine='openpyxl') if path.endswith('.xlsx') else pd.read_csv(path)

        df1, df2 = read_df(p1), read_df(p2)

        result = {}

        # Total comparison
        total1 = df1.select_dtypes(include='number').sum().sum()
        total2 = df2.select_dtypes(include='number').sum().sum()
        diff = total2 - total1
        pct = (diff / total1 * 100) if total1 else 0
        result['summary'] = {
            "file1_total": float(round(total1, 2)),
            "file2_total": float(round(total2, 2)),
            "percent_change": float(round(pct, 2)),
            "status": "increased" if pct > 0 else "decreased" if pct < 0 else "no change"
        }

        if 'Product' in df1.columns and 'Units Sold' in df1.columns:
            df1_units = df1.groupby("Product", as_index=True)["Units Sold"].sum()
            df2_units = df2.groupby("Product", as_index=True)["Units Sold"].sum()
            unit_change = (df2_units - df1_units).reindex(df1_units.index.union(df2_units.index), fill_value=0)

            increased_units = unit_change[unit_change > 0].sort_values(ascending=False)
            result["increased_units"] = [
                {
                    "product": product,
                    "increase_amount": int(change),
                    "previous_total": int(df1_units.get(product, 0)),
                    "current_total": int(df2_units.get(product, 0))
                }
                for product, change in increased_units.items()
            ]

        if 'Product' in df1.columns and 'Revenue' in df1.columns:
            df1_revenue = df1.groupby("Product", as_index=True)["Revenue"].sum()
            df2_revenue = df2.groupby("Product", as_index=True)["Revenue"].sum()
            revenue_change = (df2_revenue - df1_revenue).reindex(df1_revenue.index.union(df2_revenue.index), fill_value=0)

            increased_revenue = revenue_change[revenue_change > 0].sort_values(ascending=False)
            decreased_revenue = revenue_change[revenue_change < 0].sort_values()

            result["increased_revenue"] = [
                {
                    "product": product,
                    "increase_amount": int(change),
                    "previous_total": int(df1_revenue.get(product, 0)),
                    "current_total": int(df2_revenue.get(product, 0))
                }
                for product, change in increased_revenue.items()
            ]

            result["decreased_revenue"] = [
                {
                    "product": product,
                    "decrease_amount": int(abs(change)),
                    "previous_total": int(df1_revenue.get(product, 0)),
                    "current_total": int(df2_revenue.get(product, 0))
                }
                for product, change in decreased_revenue.items()
            ]

        pprint.pprint(result)

        # Save with current date
        filename = f"analysis_{datetime.now().strftime('%Y-%m-%d')}.json"
        full_path = os.path.join(SAVE_JSON_FOLDER, filename)
        with open(full_path, 'w', encoding='utf-8') as f:
            json.dump(result, f, indent=2, ensure_ascii=False)

        return jsonify(result)

    except Exception as e:
        return jsonify({"error": str(e)}), 500

if __name__ == '__main__':
    app.run(port=5000)
