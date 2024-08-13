import { useState, useEffect } from "react";
import DatePicker from "react-datepicker";
import "./app.scss";
import Select from "react-dropdown-select";
import axios from "axios";
import Table from "./components/Table";
import * as XLSX from "xlsx";
import { saveAs } from "file-saver";

type scanRecord = {
  Location: string;
  DateTime: string;
  PalletNo: string;
  PartNo: string;
  FullPallet: string;
  ScanTime: number;
  SQoc: string;
};

function App() {
  const currentDate = new Date();
  const [startDate, setStartDate] = useState(
    new Date(currentDate.getFullYear(), currentDate.getMonth(), 1)
  );
  const [endDate, setEndDate] = useState(
    new Date(currentDate.getFullYear(), currentDate.getMonth() + 1, 0)
  );
  const [scanRecord, setScanRecord] = useState<scanRecord[]>([]);
  const [copyRecord, setCopyRecord] = useState<scanRecord[]>([]);
  const [scanTime, setScanTime] = useState(0);
  const [byLocal, setByLocal] = useState("");
  const [byPalletNo, setByPalletNo] = useState("");

  const options = [
    {
      value: 0,
      label: "show all",
    },
    {
      value: 1,
      label: "show only 1",
    },
    {
      value: 2,
      label: "show last commit",
    },
  ];

  useEffect(() => {
    console.log(12120);
    let temp = copyRecord;

    if (scanTime === 1) {
      temp = copyRecord.filter((item) => item.ScanTime === scanTime);
    } else if (scanTime === 2) {
      const groupedByLocation = temp.reduce(
        (acc: Record<string, scanRecord>, item) => {
          if (
            !acc[item.Location] ||
            acc[item.Location].ScanTime < item.ScanTime
          ) {
            acc[item.Location] = item;
          }
          return acc;
        },
        {}
      );

      temp = Object.values(groupedByLocation);
    }

    if (byLocal.trim() !== "") {
      temp = temp.filter(
        (item) =>
          item.Location &&
          item.Location.toLowerCase().includes(byLocal.trim().toLowerCase())
      );
    }

    if (byPalletNo.trim() !== "") {
      temp = temp.filter(
        (item) =>
          item.PalletNo &&
          item.PalletNo.toLowerCase().includes(byPalletNo.trim().toLowerCase())
      );
    }

    const filteredRecords = temp.filter((record) => {
      const recordDate = new Date(record.DateTime);
      return recordDate >= startDate && recordDate <= endDate;
    });
    setScanRecord(filteredRecords);
  }, [copyRecord, startDate, endDate, scanTime, byLocal, byPalletNo]);

  useEffect(() => {
    async function getScanRecord() {
      // const result = await axios.get('http://192.168.61.104:3500/scanRecord')
      try {
        const result = await axios.get(
          "http://inventory.sumeeko.com:3001/getAllScan"
        );
        const { success, data } = result.data;
        if (success) {
          if (copyRecord.length !== data.length) {
            setCopyRecord(data);
          }
        }
      } catch (error) {
        console.error("Error fetching scan records:", error);
      }
    }
    getScanRecord();

    const interval = setInterval(() => {
      getScanRecord();
    }, 5000);

    return () => clearInterval(interval);
  }, []);

  const handleByLocalChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setByLocal(e.target.value);
  };

  const handleByPalletNo = (e: React.ChangeEvent<HTMLInputElement>) => {
    setByPalletNo(e.target.value);
  };

  const downloadExcel = () => {
    const data = scanRecord.map((record) => ({
      "Storage Space": record.Location,
      "Date Time": record.DateTime,
      "Pallet No": record.PalletNo,
      "Part No": record.PartNo,
      "Full Pallet": record.FullPallet,
      "Scan Time": record.ScanTime,
      "Quantity of carton": record.SQoc,
    }));

    // Convert data to Excel workbook
    const ws = XLSX.utils.json_to_sheet(data);
    const wb = XLSX.utils.book_new();
    XLSX.utils.book_append_sheet(wb, ws, "Scan Records");

    // Generate Excel file and trigger download
    const excelBuffer = XLSX.write(wb, { bookType: "xlsx", type: "array" });
    const blob = new Blob([excelBuffer], { type: "application/octet-stream" });
    saveAs(blob, "scan_records.xlsx");
  };

  return (
    <div className="container">
      <form className="filter">
        <fieldset className="fieldset">
          <legend className="w-auto p-2 legend">Search</legend>
          <div className="row mx-lg-5">
            <div className="col-lg-4 col-md-6 col-sm-6 form-group ">
              <label className="control-label" htmlFor="StartDate">
                Start:
              </label>
              <div className="input-group date" id="startDateTimePicker">
                <DatePicker
                  className="form-control dataTime"
                  selected={startDate}
                  onChange={(date: Date) => setStartDate(date)}
                />
              </div>
            </div>

            <div className="col-lg-4 col-md-6 col-sm-6 form-group ">
              <label className="control-label" htmlFor="StartDate">
                End:
              </label>
              <div className="input-group date" id="startDateTimePicker">
                <DatePicker
                  className="form-control dataTime"
                  selected={endDate}
                  onChange={(date: Date) => setEndDate(date)}
                />
              </div>
            </div>

            <div className="col-lg-4 col-md-6 col-sm-6 form-group ">
              <label className="control-label" htmlFor="StartDate">
                Scan Time
              </label>
              <Select
                options={options}
                values={[]}
                onChange={([{ value }]) => setScanTime(value)}
              />
            </div>

            <div className="col-lg-4 col-md-6 col-sm-6 form-group ">
              <label className="control-label" htmlFor="StartDate">
                by local
              </label>
              <div className="input-group date" id="startDateTimePicker">
                <input
                  type="text"
                  className="form-control"
                  onChange={handleByLocalChange}
                />
              </div>
            </div>

            <div className="col-lg-4 col-md-6 col-sm-6 form-group ">
              <label className="control-label" htmlFor="StartDate">
                by Pallet No
              </label>
              <div className="input-group date" id="startDateTimePicker">
                <input
                  type="text"
                  className="form-control"
                  onChange={handleByPalletNo}
                />
              </div>
            </div>

            <div className="row d-flex align-items-end">
              <div className="col-lg-4 col-md-6 col-sm-6 form-group ">
                {/* <label className="control-label" htmlFor="StartDate">Status:</label>
                  <div className="input-group date" id="startDateTimePicker">
                    <input type="text" className="form-control" placeholder="User" />
                  </div> */}
              </div>

              <div className="col-lg-4 offset-lg-4 col-md-6 col-sm-6 form-group">
                <div className="row d-flex ps-4 justify-content-between ms-lg-1 mt-2">
                  {/* <button className="col-lg-4 btn btn-primary mt-2" type="submit">Search</button> */}
                  <button
                    className="col-lg-4 btn btn-primary mt-2"
                    type="button"
                    onClick={downloadExcel}
                  >
                    Download
                  </button>
                </div>
              </div>
            </div>
          </div>
        </fieldset>
      </form>

      <Table scanRecord={scanRecord} />
    </div>
  );
}

export default App;
