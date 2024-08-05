package it.schmid.android.mofa.model;

public interface Entity {

    <R, T> R accept(Visitor<R, T> visitor, T data);

    interface Visitor<R, T> {

        R visit(Global entity, T data);

        R visit(Land entity, T data);

        R visit(Machine entity, T data);

        R visit(Task entity, T data);

        R visit(VQuarter entity, T data);

        R visit(Worker entity, T data);

        R visit(Work entity, T data);

        R visit(WorkMachine entity, T data);

        R visit(WorkVQuarter entity, T data);

        R visit(WorkWorker entity, T data);
    }
}
