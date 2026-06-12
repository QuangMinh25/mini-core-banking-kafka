import type { ReactNode } from 'react';

export type TableColumn<Row> = {
  key: string;
  header: string;
  render: (row: Row) => ReactNode;
};

type DataTableProps<Row> = {
  caption: string;
  title?: string;
  description?: string;
  columns: TableColumn<Row>[];
  rows: Row[];
  emptyMessage: string;
  actions?: ReactNode;
};

export function DataTable<Row>({
  caption,
  title,
  description,
  columns,
  rows,
  emptyMessage,
  actions,
}: DataTableProps<Row>) {
  return (
    <div className="table-card card">
      {title || description || actions ? (
        <div className="card-heading">
          <div>
            {title ? <h4>{title}</h4> : null}
            {description ? <p>{description}</p> : null}
          </div>
          {actions ? <div>{actions}</div> : null}
        </div>
      ) : null}
      <div className="table-scroll">
        <table className="data-table">
          <caption>{caption}</caption>
          <thead>
            <tr>
              {columns.map((column) => (
                <th key={column.key}>{column.header}</th>
              ))}
            </tr>
          </thead>
          <tbody>
            {rows.length > 0 ? (
              rows.map((row, index) => (
                <tr key={index}>
                  {columns.map((column) => (
                    <td key={column.key}>{column.render(row)}</td>
                  ))}
                </tr>
              ))
            ) : (
              <tr>
                <td colSpan={columns.length} className="data-table__empty">
                  {emptyMessage}
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
}
