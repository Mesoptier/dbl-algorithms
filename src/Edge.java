public class Edge
{
    /** the origin of the edge */
    private Vertex head;
    /** the destination of the edge */
    private Vertex tail;
    /** constructor to create an edge with the given origin and destination */
    public Edge(Vertex head, Vertex tail)
    {
        this.head = head;
        this.tail = tail;
    }
    /** Returns the origin of the edge. */
    public Vertex getHead()
    {
        return head;
    }
    /** Returns the destination of the edge. */
    public Vertex getTail()
    {
        return tail;
    }
    /** Removes the endpoints of the edge. */
    public void nullify()
    {
        this.head = null;
        this.tail = null;
    }
} 