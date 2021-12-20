import os
import pandas as pd
import dash
from dash import dcc
from dash import html
from dash.dependencies import Input, Output
from dash.dependencies import Input, Output
import plotly.express as px
import numpy as np
import webbrowser
from threading import Timer
import json

# load data using Python JSON module
with open('../data.json','r') as f:
    data = json.loads(f.read())
# Flatten data
df_data = pd.json_normalize(data)
df_data['date'] = pd.to_datetime(df_data['date'])
data_cols = [col for col in df_data.columns if 'data' in col]
data_cols.append('date')
fig = px.scatter(df_data[data_cols], x="date", y=data_cols)

def set_up_app():
    app = dash.Dash(__name__)
    app.layout = html.Div([
        dcc.Graph(id='line_plot', figure=fig, style={'display': 'inline-block', 'width': '100vh'})
    ])
    return app

def open_browser():
    webbrowser.open_new('http://127.0.0.1:8050/')

if __name__ == '__main__':
    Timer(1, open_browser).start()
    app = set_up_app()
    app.run_server(debug=True)