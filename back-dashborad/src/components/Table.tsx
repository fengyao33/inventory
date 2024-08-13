/* eslint-disable @typescript-eslint/no-explicit-any */
import React from "react";
import { format, toZonedTime } from "date-fns-tz";
interface Props {
  scanRecord: any[];
}

const Table: React.FC<Props> = ({ scanRecord }) => {
  // console.log(123, scanRecord)

  const formatDate = (dateString: string, timeZone: string) => {
    const zonedDate = toZonedTime(dateString, timeZone);
    return format(zonedDate, "yyyy-MM-dd HH:mm", { timeZone });
  };

  return (
    <table className="table table-striped table-light table-borderless table-creo">
      <thead className="table-dark">
        <tr>
          <th scope="col">the local</th>
          {/* <th scope="col">Date/Time (TW)</th> */}
          <th scope="col">Date/Time (MI)</th>
          <th scope="col">Pallet-No</th>
          <th scope="col">Part-No</th>
          <th scope="col">Full Pallet</th>
          <th scope="col">Quantity of carton</th>
          <th scope="col">Scan Time</th>
        </tr>
      </thead>
      <tbody>
        {scanRecord &&
          scanRecord.map((item, index) => {
            return (
              <tr key={index}>
                <td>{item.Location}</td>
                {/* <td>{"TW : " + formatDate(item.DateTime, "Asia/Taipei")}</td> */}
                <td>
                  {"MI : " + formatDate(item.DateTime, "America/Detroit")}
                </td>
                <td>{item.PalletNo}</td>
                <td>{item.PartNo}</td>
                <td>{item.FullPallet ? "Yes" : "No"}</td>
                <td>{item.SQoc}</td>
                <td>{item.ScanTime}</td>
              </tr>
            );
          })}
      </tbody>
    </table>
  );
};

export default Table;
